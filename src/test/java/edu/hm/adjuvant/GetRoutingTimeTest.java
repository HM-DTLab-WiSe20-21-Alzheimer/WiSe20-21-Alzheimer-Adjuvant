package edu.hm.adjuvant;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Test zu GetRoutingTimeTest.
 *
 * @author Anonymous Student
 */

class GetRoutingTimeTest {
    private final String adress1 = "Lothstraße 40 München";
    private final String adress2 = "Spielbudenplatz 31 Hamburg";
    private final String adress3 = "Museumsinsel 1 München";
    private final String adress4 = "Orleansstraße 50 München";
    private final String train = "öffentliche";
    private final String bycicle = "fahrrad";
    private final String car = "auto";
    private final String walk = "laufen";

    @Test void testBasic(){
        int time = new GetRoutingTime().getSeconds(adress1,adress2,car);
        Assert.assertNotEquals(0,time);
        Assert.assertNotEquals(-1,time);
    }

    @Test void testBasicDefault(){
        int time = new GetRoutingTime().getSeconds("","",car);
        Assert.assertEquals(-1,time); //default
    }


    @Test  void testGeneralCar(){
        int timeCar = new GetRoutingTime().getSeconds(adress1,adress2,car);
        Assert.assertTrue(timeCar>3*60*60);
    }

    @Test void testGeneralWalkBike() {
        int timeBycicle = new GetRoutingTime().getSeconds(adress1, adress3, bycicle);
        int timeWalk = new GetRoutingTime().getSeconds(adress1, adress3, walk);

        Assert.assertTrue(timeBycicle >15*60);
        Assert.assertTrue(timeWalk > timeBycicle);
    }

    @Test  void testGeneralTrain(){
        int timeTrain = new GetRoutingTime().getSeconds(adress1,adress4,train);
        Assert.assertTrue(timeTrain >15*60);
    }


}
