package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.classes.ReasonOfProhibitionTesting;

@Getter
@Setter
public class ResponseAboutTestingAllowed {
    private boolean testingAllowed;
    private ReasonOfProhibitionTesting reasonOfProhibition;

    public ResponseAboutTestingAllowed(boolean testingAllowed) {
        this.testingAllowed = testingAllowed;
    }

    public ResponseAboutTestingAllowed(boolean testingAllowed, ReasonOfProhibitionTesting reasonOfProhibition) {
        this.testingAllowed = testingAllowed;
        this.reasonOfProhibition = reasonOfProhibition;
    }
}
