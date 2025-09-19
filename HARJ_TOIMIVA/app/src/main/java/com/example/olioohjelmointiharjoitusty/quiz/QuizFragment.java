//Read me
//This is the cherry of the program. It is a quiz made out of the json data, amazing!

package com.example.olioohjelmointiharjoitusty.quiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olioohjelmointiharjoitusty.R;
import com.example.olioohjelmointiharjoitusty.ShowData.PopulationData;
import com.example.olioohjelmointiharjoitusty.ShowData.PopulationDataRetriever;
import com.example.olioohjelmointiharjoitusty.ShowData.WeatherParser;
import com.example.olioohjelmointiharjoitusty.ShowData.WeatherRepository;
import com.example.olioohjelmointiharjoitusty.SharedViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizFragment extends Fragment {
    private PopulationData populationData;
    private SharedViewModel sharedViewModel;
    private TextView mainQuestion, progressText;
    private RadioGroup questionsHolder;
    private Button confirmChoice;

    private List<Question> questionList;
    private WeatherParser.WeatherData weatherData;
    private boolean quizStarted = false;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int wrong = 0;

    public QuizFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mainQuestion = view.findViewById(R.id.mainQuestion);
        progressText = view.findViewById(R.id.progressText);
        questionsHolder = view.findViewById(R.id.questionsHolder);
        confirmChoice = view.findViewById(R.id.confirmChoice);

        PopulationDataRetriever popRetriever = new PopulationDataRetriever();
        popRetriever.setOnDataLoadedListener(data -> {
            populationData = data;
            attemptStart();
        });

        sharedViewModel.getCityName().observe(getViewLifecycleOwner(), kunta -> {
            if (kunta == null || kunta.isEmpty()) return;
            popRetriever.getData(requireContext(), kunta, new TextView(requireContext()));
            new Thread(() -> {
                try {
                    WeatherRepository repo = new WeatherRepository();
                    String json = repo.fetch(kunta);
                    WeatherParser.WeatherData wd = WeatherParser.parse(json);
                    requireActivity().runOnUiThread(() -> {
                        weatherData = wd;
                        attemptStart();
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Sään haku epäonnistui", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        });
    }

    private void attemptStart() {
        if (!quizStarted && populationData != null && weatherData != null) {
            quizStarted = true;
            startQuiz();
        }
    }

    private void startQuiz() {
        initQuestions();
        Collections.shuffle(questionList);
        loadQuestion();
        setupConfirmButton();
    }

    private void setupConfirmButton() {
        confirmChoice.setOnClickListener(v -> {
            int selectedId = questionsHolder.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Valitse vaihtoehto", Toast.LENGTH_SHORT).show();
                return;
            }
            int selectedIndex = questionsHolder.indexOfChild(getView().findViewById(selectedId));
            Question currentQuestion = questionList.get(currentQuestionIndex);
            if (selectedIndex == currentQuestion.getCorrectAnswerIndex()) {
                score++;
                Toast.makeText(getContext(), "Oikein!", Toast.LENGTH_SHORT).show();
            } else {
                wrong++;
                Toast.makeText(getContext(), "Väärin!", Toast.LENGTH_SHORT).show();
            }
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                loadQuestion();
            } else {
                showResultDialog();
            }
        });
    }

    private void loadQuestion() {
        questionsHolder.removeAllViews();
        Question q = questionList.get(currentQuestionIndex);
        mainQuestion.setText(q.getQuestionText());
        progressText.setText("Kysymys " + (currentQuestionIndex + 1) + "/" + questionList.size());
        for (String opt : q.getOptions()) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(opt);
            rb.setTextColor(getResources().getColor(android.R.color.white));
            rb.setId(View.generateViewId());
            questionsHolder.addView(rb);
        }
    }

    private void initQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question(
                "Kuinka monta asukasta kunnassa on?",
                new ArrayList<>(List.of(
                        String.valueOf(populationData.getPopulation()),
                        "100000", "50000", "200000"
                )), 0));
        boolean changePositive = populationData.getPopulationChangePercent() > 0;
        questionList.add(new Question(
                "Onko väestönmuutos yli vai alle 0%?",
                new ArrayList<>(List.of("Yli 0%", "Alle 0%")),
                changePositive ? 0 : 1));
        questionList.add(new Question(
                "Kuinka paljon väkiluku on muuttunut prosentteina kunnassa?",
                new ArrayList<>(List.of(
                        String.format(Locale.getDefault(), "%.1f%%", populationData.getPopulationChangePercent()),
                        "1.0%", "-1.0%", "0.0%"
                )), 0));
        questionList.add(new Question(
                "Mikä on kunnan tämänhetkinen lämpötila celsiusasteina?",
                new ArrayList<>(List.of(
                        String.format(Locale.getDefault(), "%.1f °C", weatherData.getTemperature()),
                        "0 °C", "10 °C", "-5 °C"
                )), 0));
        questionList.add(new Question(
                "Onko lämpötila alle nolla astetta kunnassa?",
                new ArrayList<>(List.of("Kyllä", "Ei")),
                weatherData.getTemperature() < 0 ? 0 : 1));
        questionList.add(new Question(
                "Mikä on kunnan tämänhetkinen tuulen keskinopeus metreinä sekunnissa?",
                new ArrayList<>(List.of(
                        String.format(Locale.getDefault(), "%.1f m/s", weatherData.getWindSpeed()),
                        "2.0 m/s", "5.0 m/s", "10.0 m/s"
                )), 0));
        questionList.add(new Question(
                "Onko tuulen nopeus yli 5.0 m/s?",
                new ArrayList<>(List.of("Kyllä", "Ei")),
                weatherData.getWindSpeed() > 5.0 ? 0 : 1));
        questionList.add(new Question(
                "Mikä on kunnan tämänhetkinen ilmankosteusprosentti?",
                new ArrayList<>(List.of(
                        String.format(Locale.getDefault(), "%.1f%%", weatherData.getHumidity()),
                        "50.0%", "60.0%", "70.0%"
                )), 0));
        questionList.add(new Question(
                "Mikä on kunnan tämänhetkinen sääkuvaus?",
                new ArrayList<>(List.of(
                        weatherData.getDescription(),
                        "Selkeä taivas", "Pilvistä", "Sataa"
                )), 0));
        List<String> iconOptions = new ArrayList<>();
        iconOptions.add(weatherData.getIconCode());
        iconOptions.add("02d");
        iconOptions.add("03d");
        iconOptions.add("10d");
        questionList.add(new Question(
                "Mikä on kunnan sääikonin koodi?",
                iconOptions,
                0));
    }

    private void showResultDialog() {
        View resultView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_quiz_result, null);
        TextView tvResult = resultView.findViewById(R.id.tvResult);
        Button btnRetry = resultView.findViewById(R.id.btnRetry);
        tvResult.setText("Quiz loppui!\nOikein: " + score + "\nVäärin: " + wrong);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(resultView)
                .setCancelable(false)
                .create();
        btnRetry.setOnClickListener(v -> {
            score = 0;
            wrong = 0;
            currentQuestionIndex = 0;
            Collections.shuffle(questionList);
            loadQuestion();
            dialog.dismiss();
        });
        dialog.show();
    }
}
