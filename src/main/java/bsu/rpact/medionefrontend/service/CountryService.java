package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.CountriesAdapter;
import bsu.rpact.medionefrontend.pojo.other.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    @Autowired
    private CountriesAdapter countriesAdapter;

    public List<Country> getAllCountries(){
        return countriesAdapter.getCountries();
    }

}
