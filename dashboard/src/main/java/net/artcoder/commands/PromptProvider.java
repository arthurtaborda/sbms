package net.artcoder.commands;

import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

@Component
public class PromptProvider extends DefaultPromptProvider {

	@Override
	public String getPrompt() {
		return "sbms-shell>";
	}
}
