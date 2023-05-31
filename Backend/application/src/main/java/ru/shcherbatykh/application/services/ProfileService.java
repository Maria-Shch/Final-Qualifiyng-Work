package ru.shcherbatykh.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.Profile;
import ru.shcherbatykh.application.repositories.ProfileRepo;

import java.util.List;

@Service
public class ProfileService {

    @Value("${app.group.quantity.profiles}")
    private int maxQuantityOfProfiles;

    private final ProfileRepo profileRepo;

    public ProfileService(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }

    public List<Profile> getProfilesSorted(){
        return profileRepo.findAll(orderByName());
    }

    private Sort orderByName() {
        return Sort.by(Sort.Direction.ASC, "name");
    }

    public Profile addNewProfile(Profile newProfile) {
        return profileRepo.save(newProfile);
    }

    public int getMaxQuantityOfProfiles(){
        return maxQuantityOfProfiles;
    }
}
