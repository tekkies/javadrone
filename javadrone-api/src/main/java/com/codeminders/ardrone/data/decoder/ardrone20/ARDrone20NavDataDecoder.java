package com.codeminders.ardrone.data.decoder.ardrone20;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavDataDecoder;
import com.codeminders.ardrone.data.ARDroneDataReader;
import com.codeminders.ardrone.data.decoder.ardrone20.navdata.ARDrone20NavData;
import com.codeminders.ardrone.data.navdata.NavDataFormatException;

public class ARDrone20NavDataDecoder extends NavDataDecoder {

    private Logger log = Logger.getLogger(this.getClass().getName());
    
    private boolean              done = false;

    byte[]                       buffer;
    
    public ARDrone20NavDataDecoder(ARDrone drone, int buffer_size) {
        super(drone);
        setName("ARDrone 2.0 NavData decodding thread");
        buffer = new byte[buffer_size];
    }

    @Override
    public void run() {
        
        super.run();
        ARDroneDataReader reader = getDataReader();
        while (!done) {
            try {
                
                pauseCheck();
                
                int len = reader.readDataBlock(buffer);

                if (len > 0) {
                    try {
                        notifyDroneWithDecodedNavdata(ARDrone20NavData.createFromData(ByteBuffer.wrap(buffer), len));
                    } catch (NavDataFormatException e) {
                        log.log(Level.SEVERE, "Failed to decode receivd navdata information", e);
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Failed to decode receivd navdata information", ex);
                    }
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, " Error reading data from data input stream. Stopping decoding thread", e);
                try {
                    reader.reconnect();
                } catch (IOException e1) {
                    log.log(Level.SEVERE, " Error reconnecting data reader", e);
                }
            }
        } 
        log.fine("Decodding thread is stopped"); 
    }

    @Override
    public void finish() {
        done = true;        
    }
}
