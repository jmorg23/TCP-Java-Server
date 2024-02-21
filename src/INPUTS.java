
import java.awt.image.BufferedImage;

public class INPUTS{
    public double temp = 5;
    public double humid = 20;
    public double pressure = 100;
    public double altitude = 10;
    //BufferedImage newImage;

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getTemp() {
        return temp;
    }
        private String value;

        public INPUTS() {
        }
    
        // Constructor with a String argument
        public INPUTS(String value) {
            this.value = value;
        }
    
        // Getter and setter methods for 'value'
    
        // Static factory method
        public static INPUTS fromString(String value) {
            return new INPUTS(value);
        }
    
    
/* 
    public BufferedImage getOutsideImage() {
        return newImage;
    }

    public void setOutsideImage(int[][][] outsideImage) {
        int width = outsideImage.length;
        int height = outsideImage[0].length;
        newImage = new BufferedImage(outsideImage.length, outsideImage[0].length, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgba = (outsideImage[x][y][3] << 24) |
                        (outsideImage[x][y][0] << 16) |
                        (outsideImage[x][y][1] << 8) |
                        outsideImage[x][y][2];
                newImage.setRGB(x, y, rgba);

            }
        }
    }
    */

    public void setTemp(double t) {
        temp = t;
    }

    public double getHumid() {
        return humid;
    }

    public void setHumid(double h) {
        humid = h;
    }

}
