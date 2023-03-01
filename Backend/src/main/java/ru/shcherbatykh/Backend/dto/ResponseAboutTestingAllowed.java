package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.classes.ReasonOfProhibitionTesting;

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
