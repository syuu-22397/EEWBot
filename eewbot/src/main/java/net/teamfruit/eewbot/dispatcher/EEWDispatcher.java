package net.teamfruit.eewbot.dispatcher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.teamfruit.eewbot.EEWBot;

public class EEWDispatcher implements Runnable {

	public static final String REMOTE = "http://www.kmoni.bosai.go.jp/new/webservice/hypo/eew/";
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public void run() {
		try {
			final Date date = new Date(System.currentTimeMillis()+EEWBot.ntp.getOffset()-1000);
			final URL url = new java.net.URL(REMOTE+FORMAT.format(date)+".json");
			final EEW res = EEWBot.GSON.fromJson(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8), EEW.class);
			if (res.alertflg!=null) { //EEW
				final EEWEvent event = new EEWEvent(EEWBot.client, res);
				EEWBot.client.getDispatcher().dispatch(event);
			}
		} catch (final JsonSyntaxException|JsonIOException|IOException e) {
			EEWBot.LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public static class EEW {
		private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		private String alertflg;
		private String calcintensity;
		private String depth;
		private boolean is_cancel;
		private boolean is_final;
		private boolean is_training;
		private String latitude;
		private String longitude;
		private String magunitude;
		private String origin_time;
		private String region_code;
		private String region_name;
		private String report_id;
		private String report_num;
		private String report_time;
		private String request_hypo_type;
		private String request_time;
		private Result result;
		private Security security;

		public static class Result {
			private boolean is_auth;
			private String message;
			private String status;

			public boolean isAuth() {
				return this.is_auth;
			}

			public String getMessage() {
				return this.message;
			}

			public boolean getStatus() {
				return "success".equals(this.status);
			}
		}

		public static class Security {
			private String hash;
			private String realm;

			public String getHash() {
				return this.hash;
			}

			public String getReam() {
				return this.realm;
			}
		}

		public boolean isAlart() {
			return this.alertflg.equals("警報");
		}

		public String getIntensity() {
			return this.calcintensity;
		}

		public int getDepth() {
			if (StringUtils.isEmpty(this.depth))
				return -1;
			return Integer.parseInt(this.depth.substring(0, this.depth.length()-2));
		}

		public boolean isCancel() {
			return this.is_cancel;
		}

		public boolean isFinal() {
			return this.is_final;
		}

		public boolean isTraining() {
			return this.is_training;
		}

		public float getLat() {
			if (StringUtils.isEmpty(this.depth))
				return -1;
			return Float.parseFloat(this.latitude);
		}

		public float getLon() {
			if (StringUtils.isEmpty(this.depth))
				return -1;
			return Float.parseFloat(this.longitude);
		}

		public float getMagnitude() {
			if (StringUtils.isEmpty(this.depth))
				return -1;
			return Float.parseFloat(this.magunitude);
		}

		public Date getOriginTime() {
			if (StringUtils.isEmpty(this.depth))
				return null;
			try {
				return EEWDispatcher.FORMAT.parse(this.origin_time);
			} catch (final ParseException e) {
				return null;
			}
		}

		@Deprecated
		public int getRegionCode() {
			if (StringUtils.isEmpty(this.depth))
				return -1;
			return Integer.parseInt(this.region_code);
		}

		public String getRegionName() {
			return this.region_name;
		}

		public long getReportId() {
			if (StringUtils.isEmpty(this.depth))
				return -1;
			return Long.parseLong(this.report_id);
		}

		public int gerReportNum() {
			if (StringUtils.isEmpty(this.depth))
				return -1;
			return Integer.parseInt(this.report_num);
		}

		public Date getReportTime() {
			if (StringUtils.isEmpty(this.depth))
				return null;
			try {
				return FORMAT.parse(this.report_time);
			} catch (final ParseException e) {
				return null;
			}
		}

		public String getRequestHypoType() {
			return this.request_hypo_type;
		}

		public Date getRequestTime() {
			if (StringUtils.isEmpty(this.depth))
				return null;
			try {
				return EEWDispatcher.FORMAT.parse(this.request_time);
			} catch (final ParseException e) {
				return null;
			}
		}
	}
}
