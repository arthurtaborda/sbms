package net.artcoder.commands;

import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
public class BannerProvider extends DefaultBannerProvider  {

	public String getBanner() {
		StringBuilder buf = new StringBuilder();
		buf.append("=======================================").append(OsUtils.LINE_SEPARATOR);
		buf.append("*                                     *").append(OsUtils.LINE_SEPARATOR);
		buf.append("*   SUPER BACKUP  MANAGEMENT SYSTEM   *").append(OsUtils.LINE_SEPARATOR);
		buf.append("*                                     *").append(OsUtils.LINE_SEPARATOR);
		buf.append("=======================================").append(OsUtils.LINE_SEPARATOR);
		return buf.toString();
	}
	
	@Override
	public String getProviderName() {
		return "SBMS Banner";
	}

	@Override
	public String getWelcomeMessage() {
		return "Prepare to be amazed";
	}
}