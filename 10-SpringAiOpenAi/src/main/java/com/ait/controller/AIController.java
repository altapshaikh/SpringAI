package com.ait.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AIController {

	//private OpenAiChatModel openAiChatModel;
	private ChatClient chatClient;
	private OpenAiImageModel imageModel;
	
	/*
	 * public AIController(OpenAiChatModel openAiChatModel) {
	 * this.chatClient=ChatClient.create(openAiChatModel); }
	 */
	
	
	public AIController(OpenAiImageModel imageModel,ChatClient.Builder builder) {
		this.imageModel=imageModel;
		this.chatClient=builder.build();
	}
	
	@GetMapping(value = "/apis/{prompt}")
	public String generateResult(@PathVariable("prompt") String prompt) {
		String response = chatClient.prompt(prompt).call().content();
		return response;
	}
	
	@GetMapping(value = "/api2s/{prompt}")
	public String generateAdavanceResult(@PathVariable("prompt") String prompt) {
		ChatResponse chatResponse = chatClient.prompt(prompt).call().
				chatResponse();
		System.out.println(chatResponse.getMetadata().getModel());
		  AssistantMessage output = chatResponse.getResult().getOutput();
		return output.getText();
	}
	
	@GetMapping(value = "/api3/{prompt}")
	public String generateImage(@PathVariable("prompt") String prompt) {
		boolean contains = prompt.contains("image");
		if(contains) {
			ImagePrompt prompt1=new ImagePrompt(prompt);
			ImageResponse response = imageModel.call(prompt1);
			String url = response.getResult().getOutput().getUrl();
			return url;
		}else {
		
			ChatResponse chatResponse = chatClient.prompt(prompt).call().
					chatResponse();
			System.out.println(chatResponse.getMetadata().getModel());
			  AssistantMessage output = chatResponse.getResult().getOutput();
			return output.getText();
		}
	}
	
	//describe the image
	@PostMapping("/info/{prompt}")
	public String describeImage(@PathVariable("prompt")String prompt,@RequestParam("file")MultipartFile file) {
		System.out.println(file.getOriginalFilename());
		String content = chatClient.prompt().user(use -> use.text(prompt)
				.media(MimeTypeUtils.IMAGE_JPEG, file.getResource()))
		.call().content();
		return content;
		
	}
	
	
}
