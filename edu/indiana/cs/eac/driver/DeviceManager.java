package edu.indiana.cs.eac.driver;


public class DeviceManager
{
	private String offlineDrivers[];
	private String ethernetDrivers[];
	private String usbDrivers[];
	private String driverList[][];
	
	private boolean driverConnected;
	private Device driver;

	private static int driver_flag = 0;
	public static final int NULL_DRIVERS = driver_flag++;
	public static final int EAC_DRIVERS  = driver_flag++;
	public static final int UEAC_DRIVERS = driver_flag++;
	
	public DeviceManager()
	{
		
	}
	
	
	public String[][] getDeviceList()
	{
		// populate the driver list
		offlineDrivers  = NullDriver.getDeviceList();
		ethernetDrivers = EthernetDriver.getDeviceList();
//		usbDrivers      = USBDriver.getDeviceList2(lf);
		driverList      = new String[][]
			{
				offlineDrivers,
				ethernetDrivers,
//				usbDrivers
			};
		
		driverConnected = false;
		
		return driverList;
	}
	
	public int getNumDevices()
	{
		return 0;
	}
	
	public boolean validateDevice(Device device)
	{
//		return device.validate();
		return true;
	}
}
