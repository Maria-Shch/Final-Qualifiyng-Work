package ru.shcherbatykh.autochecker.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.error_handling.ErrorCode;
import ru.shcherbatykh.autochecker.error_handling.RequestValidationException;
import ru.shcherbatykh.autochecker.model.CodeSource;

import java.util.List;

@Service
public class InputRequestValidationService {
    public void validateInputRequestParameters(List<CodeSource> codeSources) {
        if (codeSources == null || codeSources.isEmpty()) {
            throw new RequestValidationException(ErrorCode.ACH_001);
        }
    }
}
