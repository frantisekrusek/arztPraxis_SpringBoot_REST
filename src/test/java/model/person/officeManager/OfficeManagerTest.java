package model.person.officeManager;

import static org.junit.jupiter.api.Assertions.*;

import model.Template;
import service.Clerk;
import service.Office;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.OfficeManager;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

class OfficeManagerTest {

    private OfficeManager officeManager;
    private Template mockTemplate;

    @BeforeEach
    void setOfficeManager() {
        this.officeManager = new OfficeManager();
        officeManager.setOffice(new Office(new Clerk(), new OfficeManager()));
    }

    @BeforeEach
    void createMockTemplate() {
        mockTemplate = new Template(DayOfWeek.MONDAY, LocalTime.now());
    }

    @Test
    @DisplayName("prevent duplicates")
    void testCategorizeTemplate() {
        assertTrue(officeManager.categorizeTemplate(mockTemplate));
        LocalTime sameTime = mockTemplate.getStartTime();
        DayOfWeek sameDay = mockTemplate.getWeekday();
        Template anotherTemplate = new Template(sameDay, sameTime);
        assertFalse(officeManager.categorizeTemplate(anotherTemplate));
    }

    //frequent errors expected
    @Test
    @DisplayName("Sunday-Template goes into index 0?")
    void testCategorizeTemplate_2() {
        Template sundayTemplate = new Template(DayOfWeek.SUNDAY, LocalTime.now());
        officeManager.categorizeTemplate(sundayTemplate);
        officeManager.categorizeTemplate(mockTemplate);
        Set[] templates = officeManager.getOffice().getTemplates();
        Set<Template> set = new HashSet<Template>();
        set.add(sundayTemplate);
        assertIterableEquals(templates[0],set);
    }
}
