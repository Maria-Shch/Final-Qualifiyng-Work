package ru.shcherbatykh.application.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.FormOfEdu;
import ru.shcherbatykh.application.repositories.FormOfEduRepo;

import java.util.List;

@Service
public class FormOfEduService {
    private final FormOfEduRepo formOfEduRepo;

    public FormOfEduService(FormOfEduRepo formOfEduRepo) {
        this.formOfEduRepo = formOfEduRepo;
    }


    public List<FormOfEdu> getFormsOfEduSorted(){
        return formOfEduRepo.findAll(orderByName());
    }

    private Sort orderByName() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
