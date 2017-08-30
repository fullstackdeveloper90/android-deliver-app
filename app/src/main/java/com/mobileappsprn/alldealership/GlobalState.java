package com.mobileappsprn.alldealership;

import java.util.ArrayList;

import com.mobileappsprn.alldealership.entities.Details;
import com.mobileappsprn.alldealership.entities.Items;
import com.mobileappsprn.alldealership.entities.RootItems;

public class GlobalState {
	public static ArrayList<Items> menuItems;
	public static ArrayList<Items> homeMenuItems;
	
	public static ArrayList<RootItems> rootMenuItems;
	public static ArrayList<Details> menuItemDetails;
	
	public static ArrayList<String> carMake;
	public static ArrayList<String> carModel;
	public static String selectedCar;
	public static int selectedCarIndex;
	public static ArrayList<String> uploadedImagePath;
	public static boolean isFirstTimeLaunch = false;
	//public static ArrayList<String> vehicleName;
	//public static ArrayList<String> carName;
	public static String serverId;
	public static String dealershipGroupVersion;
    public static String menuType;
    public static String rowBGColor1;
    public static String rowFGColor1;
    public static String rowBGColor2;
    public static String rowFGColor2;
    public static String uiTitle;
	public static String footerText;
	public static String dealerWebsite;
	public static boolean isDealershipGroupVersion;
    public static boolean isNewMenu;
    public static boolean isNewSubMenu;

	
	// specific dealership information
	public static String dealershipAddress;
	public static String dealershipLat;
	public static String dealershipLon;
	public static String dealershipPhone;
	public static String dealershipEmail;
    public static String dealershipId;
	
	public static String tempdealershipAddress;
	public static String tempdealershipLat;
	public static String tempdealershipLon;
	public static String tempdealershipPhone;
	public static String tempdealershipEmail;
	public static String tempdealerWebsite;
	public static String logoUrl;
	
	public static String dealershipServicePhone;
	public static String tempdealershipServicePhone;

    public static String locationName;
	public static String templocationName;

	public static long REGISTRATION_REMAINDER_TIMELAP = 2592000000L;


	public static boolean isFromMoreOptions = false;
	public static boolean isRegisterRemainderEnable = true;
	public static boolean isVariantTcc = false;

	
}
