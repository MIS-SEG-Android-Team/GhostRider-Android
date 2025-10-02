package org.rmj.guanzongroup.pacitareward;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JsonPutTest {
    @Test
    public void addition_isCorrect() {
        try {

            String loJson = "{name: 'John', age:30, 'car':null}";
            JSONObject loInput = new JSONObject(loJson);

            System.out.println("Old data: " + loInput);

            loInput.put("name", "Arcilla");

            System.out.println("New data: " + loInput);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}