//read me
//This class is made to share data between fragmens

package com.example.olioohjelmointiharjoitusty;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<String> cityName = new MutableLiveData<>();

    public void setCityName(String city) {
        cityName.setValue(city);
    }

    //live data getter here
    public LiveData<String> getCityName() {
        return cityName;
    }
}
