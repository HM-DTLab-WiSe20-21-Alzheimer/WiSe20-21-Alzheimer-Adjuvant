package edu.hm.adjuvant;

import com.amazon.ask.model.services.reminderManagement.RecurrenceDay;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Test zu HandleFrequence.
 *
 * @author Anonymous Student
 */

class HandleFrequenceTest {

    @Test void dayToRecurranceDayMonday(){
        RecurrenceDay given = new HandleFrequence().getDay("montag");
        Assert.assertEquals(RecurrenceDay.MO, given);
    }
    @Test void dayToRecurranceDayTuesday(){
        RecurrenceDay given = new HandleFrequence().getDay("dienstag");
        Assert.assertEquals(RecurrenceDay.TU, given);
    }
    @Test void dayToRecurranceDayWednesday(){
        RecurrenceDay given = new HandleFrequence().getDay("mittwoch");
        Assert.assertEquals(RecurrenceDay.WE, given);
    }
    @Test void dayToRecurranceDayThursday(){
        RecurrenceDay given = new HandleFrequence().getDay("donnerstag");
        Assert.assertEquals(RecurrenceDay.TH, given);
    }
    @Test void dayToRecurranceDayFrieday(){
        RecurrenceDay given = new HandleFrequence().getDay("freitag");
        Assert.assertEquals(RecurrenceDay.FR, given);
    }
    @Test void dayToRecurranceDaySaturday(){
        RecurrenceDay given = new HandleFrequence().getDay("samstag");
        Assert.assertEquals(RecurrenceDay.SA, given);
    }
    @Test void dayToRecurranceDaySunday(){
        RecurrenceDay given = new HandleFrequence().getDay("sonntag");
        Assert.assertEquals(RecurrenceDay.SU, given);
    }
    @Test void dayToRecurranceDayDefault(){
        RecurrenceDay given = new HandleFrequence().getDay("eiscreme");
        Assert.assertEquals(RecurrenceDay.UNKNOWN_TO_SDK_VERSION, given);
    }
}
