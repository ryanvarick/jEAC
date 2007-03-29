package edu.indiana.cs.eac.driver;


public class DriverManager
{
	private String offlineDrivers[];
	private String ethernetDrivers[];
	private String usbDrivers[];
	private String driverList[][];
	
	private boolean driverConnected;
	private HAL driver;

	private static int driver_flag = 0;
	public static final int NULL_DRIVERS = driver_flag++;
	public static final int EAC_DRIVERS  = driver_flag++;
	public static final int UEAC_DRIVERS = driver_flag++;
	
	public DriverManager()
	{
		
	}
	
	
	public void buildDriverList()
	{
		// populate the driver list
		offlineDrivers  = NullDriver.getDeviceList();
		ethernetDrivers = EthernetDriver.getDeviceList();
		usbDrivers      = USBDriver.getDeviceList2(lf);
		driverList      = new String[][]
			{
				offlineDrivers,
				ethernetDrivers,
				usbDrivers
			};
		
		driverConnected = false;
		
	}
}
