package org.lordsofchaos;

import org.junit.Test;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

import java.io.*;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class SerializationTest
{
    @Test
    public void testRealWorldCoordinatesAreSerializable() throws IOException, ClassNotFoundException {
        RealWorldCoordinates rwc = new RealWorldCoordinates(34, 12);
        
        //Serialize Object
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(rwc);
        byte[] bytes = bout.toByteArray();
        
        //Deserialize Object
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream ooin = new ObjectInputStream(bin);
        RealWorldCoordinates newRwc = (RealWorldCoordinates) ooin.readObject();
        
        //Check that they are equal
        assertThat(newRwc, samePropertyValuesAs(rwc));
    }
}
