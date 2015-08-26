/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2014 Zhenghong Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.wallfly.lockdapp.weatherutils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.hmkcode.android.recyclerview.R;

import io.wallfly.lockdapp.lockutils.Utils;

/**
 * A wrapper for all weather information provided by Yahoo weather apis.
 * @author Zhenghong Wang
 */
public class WeatherInfo {
	
	String mTitle = "";
	String mDescription = "";
	String mLanguage = "";
	String mLastBuildDate = "";
	String mLocationCity = "";
	String mLocationRegion = "";
	String mLocationCountry = "";
	
	String mWindChill = "";
	String mWindDirection = "";
	String mWindSpeed = "";
	
	String mAtmosphereHumidity = "";
	String mAtmosphereVisibility = "";
	String mAtmospherePressure = "";
	String mAtmosphereRising = "";
	
	String mAstronomySunrise = "";
	String mAstronomySunset = "";
	
	String mConditionTitle = "";
	String mConditionLat = "";
	String mConditionLon = "";

	/*
	 * information in tag "yweather:condition"
	 */
	int mCurrentCode = 0;
	String mCurrentText = "";
	/** 
	 * Default in Celsius
	 * You can set it by {@link YahooWeather#setUnit(char)}
	 */
	int mCurrentTemp = 0;
	String mCurrentConditionIconURL = "";
	Bitmap mCurrentConditionIcon = null;
	String mCurrentConditionDate = "";

	/*
	 * information in the first tag "yweather:forecast"
	 */

	/*
	 * information in the second tag "yweather:forecast"
	 */
	
	ForecastInfo mForecastInfo1 = new ForecastInfo();
	ForecastInfo mForecastInfo2 = new ForecastInfo();
	ForecastInfo mForecastInfo3 = new ForecastInfo();
	ForecastInfo mForecastInfo4 = new ForecastInfo();
	ForecastInfo mForecastInfo5 = new ForecastInfo();
	private List<ForecastInfo> mForecastInfoList = null;
	
	/*
	 * detail location info from woeid result
	 */
	String mWOEIDneighborhood = "";
	String mWOEIDCounty = "";
	String mWOEIDCountry = "";
	String mWOEIDState = "";
	
	public WeatherInfo() {
		mForecastInfoList = new ArrayList<ForecastInfo>();
		mForecastInfoList.add(mForecastInfo1);
		mForecastInfoList.add(mForecastInfo2);
		mForecastInfoList.add(mForecastInfo3);
		mForecastInfoList.add(mForecastInfo4);
		mForecastInfoList.add(mForecastInfo5);
	}

	public List<ForecastInfo> getForecastInfoList() {
		return mForecastInfoList;
	}

	protected void setForecastInfoList(List<ForecastInfo> forecastInfoList) {
		mForecastInfoList = forecastInfoList;
	}

	public ForecastInfo getForecastInfo1() {
		return mForecastInfo1;
	}

	protected void setForecastInfo1(ForecastInfo forecastInfo1) {
		mForecastInfo1 = forecastInfo1;
	}

	public ForecastInfo getForecastInfo2() {
		return mForecastInfo2;
	}

	protected void setForecastInfo2(ForecastInfo forecastInfo2) {
		mForecastInfo2 = forecastInfo2;
	}

	public ForecastInfo getForecastInfo3() {
		return mForecastInfo3;
	}

	protected void setForecastInfo3(ForecastInfo forecastInfo3) {
		mForecastInfo3 = forecastInfo3;
	}

	public ForecastInfo getForecastInfo4() {
		return mForecastInfo4;
	}

	protected void setForecastInfo4(ForecastInfo forecastInfo4) {
		mForecastInfo4 = forecastInfo4;
	}

	public ForecastInfo getForecastInfo5() {
		return mForecastInfo5;
	}

	protected void setForecastInfo5(ForecastInfo forecastInfo5) {
		mForecastInfo5 = forecastInfo5;
	}

	public String getCurrentConditionDate() {
		return mCurrentConditionDate;
	}
	
	protected void setCurrentConditionDate(String currentConditionDate) {
		mCurrentConditionDate = currentConditionDate;
	}
	
	public int getCurrentCode() {
		return mCurrentCode;
	}

	protected void setCurrentCode(int currentCode) {
		mCurrentCode = currentCode;
		mCurrentConditionIconURL = getIconUri(currentCode);
	}


    private String getIconUri(int forecastCode) {
        String icon = "";
        switch (forecastCode){
            case 0:
                icon = Utils.getString(R.string.wi_tornado);
                break;

            case 1 :
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 2:
                icon = Utils.getString(R.string.wi_hurricane);
                break;

            case 3:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 4:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 5:
                icon = Utils.getString(R.string.wi_night_showers);
                break;

            case 6:
                icon = Utils.getString(R.string.wi_night_showers);
                break;

            case 7:
                icon = Utils.getString(R.string.wi_night_showers);
                break;

            case 8:
                icon = Utils.getString(R.string.wi_hail);
                break;

            case 9:
                icon = Utils.getString(R.string.wi_sprinkle);
                break;

            case 10:
                icon = Utils.getString(R.string.wi_hail);
                break;

            case 11:
                icon = Utils.getString(R.string.wi_showers);
                break;

            case 12:
                icon = Utils.getString(R.string.wi_showers);
                break;

            case 13:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 14:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 15:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 16:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 17:
                icon = Utils.getString(R.string.wi_hail);
                break;

            case 18:
                icon = Utils.getString(R.string.wi_hail);
                break;

            case 19:
                icon = Utils.getString(R.string.wi_night_sleet_storm);
                break;

            case 20:
                icon = Utils.getString(R.string.wi_fog);
                break;

            case 21:
                icon = Utils.getString(R.string.wi_fog);
                break;

            case 22:
                icon = Utils.getString(R.string.wi_fog);
                break;

            case 23:
                icon = Utils.getString(R.string.wi_fog);
                break;

            case 24:
                icon = Utils.getString(R.string.wi_night_cloudy_windy);
                break;

            case 25:
                icon = Utils.getString(R.string.wi_snowflake_cold);
                break;

            case 26:
                icon = Utils.getString(R.string.wi_night_cloudy);
                break;

            case 27:
                icon = Utils.getString(R.string.wi_night_cloudy);
                break;

            case 28:
                icon = Utils.getString(R.string.wi_night_cloudy);
                break;

            case 29:
                icon = Utils.getString(R.string.wi_night_cloudy);
                break;

            case 30:
                icon = Utils.getString(R.string.wi_day_cloudy);
                break;

            case 31:
                icon = Utils.getString(R.string.wi_night_clear);
                break;

            case 32:
                icon = Utils.getString(R.string.wi_day_sunny);
                break;

            case 33:
                icon = Utils.getString(R.string.wi_night_clear);
                break;

            case 34:
                icon = Utils.getString(R.string.wi_day_sunny);
                break;

            case 35:
                icon = Utils.getString(R.string.wi_night_alt_rain_mix);
                break;

            case 36:
                icon = Utils.getString(R.string.wi_hot);
                break;

            case 37:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 38:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 39:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 40:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 41:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 42:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 43:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 44:
                icon = Utils.getString(R.string.wi_cloudy);
                break;

            case 45:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            case 46:
                icon = Utils.getString(R.string.wi_snow);
                break;

            case 47:
                icon = Utils.getString(R.string.wi_thunderstorm);
                break;

            default:
                icon = Utils.getString(R.string.wi_alien);
                break;
        }

        return icon;
    }
	
	protected void setCurrentTemp(int currentTemp) {
	    mCurrentTemp = currentTemp;
	}
	
	public int getCurrentTemp() {
		return mCurrentTemp;
	}

	public String getTitle() {
		return mTitle;
	}

	protected void setTitle(String title) {
		mTitle = title;
	}

	public String getDescription() {
		return mDescription;
	}

	protected void setDescription(String description) {
		mDescription = description;
	}

	public String getLanguage() {
		return mLanguage;
	}

	protected void setLanguage(String language) {
		mLanguage = language;
	}

	public String getLastBuildDate() {
		return mLastBuildDate;
	}

	protected void setLastBuildDate(String lastBuildDate) {
		mLastBuildDate = lastBuildDate;
	}

	public String getLocationCity() {
		return mLocationCity;
	}

	protected void setLocationCity(String locationCity) {
		mLocationCity = locationCity;
	}

	public String getLocationRegion() {
		return mLocationRegion;
	}

	protected void setLocationRegion(String locationRegion) {
		mLocationRegion = locationRegion;
	}

	public String getLocationCountry() {
		return mLocationCountry;
	}

	protected void setLocationCountry(String locationCountry) {
		mLocationCountry = locationCountry;
	}

	public String getWindChill() {
		return mWindChill;
	}

	protected void setWindChill(String windChill) {
		mWindChill = windChill;
	}

	public String getWindDirection() {
		return mWindDirection;
	}

	protected void setWindDirection(String windDirection) {
		mWindDirection = windDirection;
	}

	public String getWindSpeed() {
		return mWindSpeed;
	}

	protected void setWindSpeed(String windSpeed) {
		mWindSpeed = windSpeed;
	}

	public String getAtmosphereHumidity() {
		return mAtmosphereHumidity;
	}

	protected void setAtmosphereHumidity(String atmosphereHumidity) {
		mAtmosphereHumidity = atmosphereHumidity;
	}

	public String getAtmosphereVisibility() {
		return mAtmosphereVisibility;
	}

	protected void setAtmosphereVisibility(String atmosphereVisibility) {
		mAtmosphereVisibility = atmosphereVisibility;
	}

	public String getAtmospherePressure() {
		return mAtmospherePressure;
	}

	protected void setAtmospherePressure(String atmospherePressure) {
		mAtmospherePressure = atmospherePressure;
	}

	public String getAtmosphereRising() {
		return mAtmosphereRising;
	}

	protected void setAtmosphereRising(String atmosphereRising) {
		mAtmosphereRising = atmosphereRising;
	}

	public String getAstronomySunrise() {
		return mAstronomySunrise;
	}

	protected void setAstronomySunrise(String astronomySunrise) {
		mAstronomySunrise = astronomySunrise;
	}

	public String getAstronomySunset() {
		return mAstronomySunset;
	}

	protected void setAstronomySunset(String astronomySunset) {
		mAstronomySunset = astronomySunset;
	}

	public String getConditionTitle() {
		return mConditionTitle;
	}

	protected void setConditionTitle(String conditionTitle) {
		mConditionTitle = conditionTitle;
	}

	public String getConditionLat() {
		return mConditionLat;
	}

	protected void setConditionLat(String conditionLat) {
		mConditionLat = conditionLat;
	}

	public String getConditionLon() {
		return mConditionLon;
	}

	protected void setConditionLon(String conditionLon) {
		mConditionLon = conditionLon;
	}

	public String getCurrentText() {
		return mCurrentText;
	}

	protected void setCurrentText(String currentText) {
		mCurrentText = currentText;
	}

	public String getCurrentConditionIconURL() {
		return mCurrentConditionIconURL;
	}

	public Bitmap getCurrentConditionIcon() {
		return mCurrentConditionIcon;
	}

	protected void setCurrentConditionIcon(Bitmap mCurrentConditionIcon) {
		this.mCurrentConditionIcon = mCurrentConditionIcon;
	}

	protected void setWOEIDneighborhood(String wOEIDneighborhood) {
        mWOEIDneighborhood = wOEIDneighborhood;
    }

    protected void setWOEIDCounty(String wOEIDCounty) {
        mWOEIDCounty = wOEIDCounty;
    }

    protected void setWOEIDCountry(String wOEIDCountry) {
        mWOEIDCountry = wOEIDCountry;
    }

    protected void setWOEIDState(String wOEIDState) {
        mWOEIDState = wOEIDState;
    }

    public String getWOEIDneighborhood() {
        return mWOEIDneighborhood;
    }

    public String getWOEIDCounty() {
        return mWOEIDCounty;
    }

    public String getWOEIDCountry() {
        return mWOEIDCountry;
    }

    public String getWOEIDState() {
        return mWOEIDState;
    }

    public class ForecastInfo {
		private String mForecastDay;
		private String mForecastDate;
		private int mForecastCode;
		/** 
		 * Default in Celsius
		 * You can set it by {@link YahooWeather#setUnit(char)}
		 */
		private int mForecastTempHigh;
		private int mForecastTempLow;
		private String mForecastConditionIconURL;
		private Bitmap mForecastConditionIcon;
		private String mForecastText;

		public Bitmap getForecastConditionIcon() {
			return mForecastConditionIcon;
		}

		protected void setForecastConditionIcon(Bitmap mForecastConditionIcon) {
			this.mForecastConditionIcon = mForecastConditionIcon;
		}

		public String getForecastDay() {
			return mForecastDay;
		}

		protected void setForecastDay(String forecastDay) {
			mForecastDay = forecastDay;
		}

		public String getForecastDate() {
			return mForecastDate;
		}

		protected void setForecastDate(String forecastDate) {
			mForecastDate = forecastDate;
		}

		public int getForecastCode() {
			return mForecastCode;
		}

		protected void setForecastCode(int forecastCode) {
			mForecastCode = forecastCode;
		}



        public int getForecastTempHigh() {
			return mForecastTempHigh;
		}

		protected void setForecastTempHigh(int forecastTempHigh) {
			mForecastTempHigh = forecastTempHigh;
		}

		public int getForecastTempLow() {
			return mForecastTempLow;
		}

		protected void setForecastTempLow(int forecastTempLow) {
			mForecastTempLow = forecastTempLow;
		}

		public String getForecastConditionIconURL() {
			return mForecastConditionIconURL;
		}

		public String getForecastText() {
			return mForecastText;
		}

		protected void setForecastText(String forecastText) {
			mForecastText = forecastText;
		}
		
	}
}
