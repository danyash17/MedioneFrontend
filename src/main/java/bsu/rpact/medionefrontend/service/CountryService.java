package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.CountriesAdapter;
import bsu.rpact.medionefrontend.pojo.other.Country;
import bsu.rpact.medionefrontend.pojo.other.RestCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    @Autowired
    private CountriesAdapter countriesAdapter;

    public List<RestCountry> getAllCountries(){
        return countriesAdapter.getCountries();
    }

}
