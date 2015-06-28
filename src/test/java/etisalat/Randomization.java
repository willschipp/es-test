package etisalat;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.springframework.util.StopWatch;


public class Randomization {
	
	Random random = new Random();
	SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");

	//sample lines
	//210.8.79.228    201504300100609 GET http://www.fas.harvard.edu/~hpcws/nav/navbar_r4_c3.jpg  0   TCP_MISS/200    -   188.139.105.139 -   0
	//210.8.79.228    201504300100609 GET http://data.kasabi.com/dataset/bricklink/set/4193-1/inventory/60477-120  0   TCP_MISS/200    -   188.139.105.139 -   0 

	@Test
	public void generator() throws Exception {
		//build up a map (7 keys) randomizing ip, timestamp, urlfactor and target ip
		FileOutputStream fos = new FileOutputStream("/tmp/onebillion.log");
		StopWatch watch = new StopWatch();
		watch.start();
		for (int i=0;i<100000000;i++) {
			String[] parts = new String[10];
			parts[0] = randomIP();
			parts[1] = formattedDate();
			parts[2] = "GET";
			parts[3] = randomURL();
			parts[4] = "0";
			parts[5] = "TCP_MISS/200";
			parts[6] = "-";
			parts[7] = randomIP();
			parts[8] = "-";
			parts[9] = "0";
			//build a String
			StringBuilder builder = new StringBuilder();
			for (String part : parts) {
				if (builder.length() > 0) {
					builder.append(" ");
				}//end if
				builder.append(part);
			}//end for
			//write to a file
			fos.write(builder.toString().getBytes());
			fos.write("\n".getBytes());
			fos.flush();
		}//end for
		fos.close();
		watch.stop();
		System.out.println(watch.getLastTaskTimeMillis());
		
	}
	
	
	private String formattedDate() {
		return sdf.format(new Date());
	}
	
	private String randomURL() {
		//random components
		String[] parts = new String[4];
		for (int i=0;i<parts.length;i++) {
			parts[i] = UUID.randomUUID().toString();
		}//end
		boolean started = false;
		StringBuilder builder = new StringBuilder("http://");
		for (String part : parts) {
			if (started) {
				builder.append("/");
			} else {
				started = true;
			}//end if
			builder.append(part);
		}//end for
		return builder.toString();
	}
	
	private String randomIP() {
		int[] ipAddress = new int[4];
		for (int i=0;i<ipAddress.length;i++) {
			ipAddress[i] = random.nextInt((255 - 1) + 1) + 1;
		}//end for
		StringBuilder builder = new StringBuilder();
		for (int address : ipAddress) {
			if (builder.length() > 0) {
				builder.append(".");
			}//end if
			builder.append(address);
		}//end for
		return builder.toString();
		
	}
}
