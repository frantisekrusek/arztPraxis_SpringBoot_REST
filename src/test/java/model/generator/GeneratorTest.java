package model.generator;

import model.appointment.Template;
import model.office.Office;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {
    private Template mockTemplate;

    @BeforeEach
    void createMockTemplate() {
        mockTemplate = new Template(DayOfWeek.MONDAY, LocalTime.now());
    }

    @Test
    void testGenerateAppsFromSingleTemplate() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> new Generator().generateAppsFromSingleTemplate(mockTemplate,-3, Instant.now()));
    }
}