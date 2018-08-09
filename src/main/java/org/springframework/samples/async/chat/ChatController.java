package org.springframework.samples.async.chat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/mvc/chat")
public class ChatController {

	private final ChatRepository chatRepository;

	private final Map<DeferredResult<List<String>>, Integer> chatRequests =
			new ConcurrentHashMap<DeferredResult<List<String>>, Integer>();


	@Autowired
	public ChatController(ChatRepository chatRepository) {
		this.chatRepository = chatRepository;
	}

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getMessages() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("chat");
		return modelAndView;
	}

	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public DeferredResult<List<String>> getMessages(@RequestParam int messageIndex) {

		//获取消息的时候创建一个新的deferredResult保存到内存中,当异步请求完成的时候，移除这个result
		final DeferredResult<List<String>> deferredResult = new DeferredResult<List<String>>(null, Collections.emptyList());
		this.chatRequests.put(deferredResult, messageIndex);

		deferredResult.onCompletion(new Runnable() {
			@Override
			public void run() {
				chatRequests.remove(deferredResult);
			}
		});

		/**
		 * 检查是否有新的消息
		 * 1 如果有这个控制器会立即返回
		 * 2 否则 会稍后返回，直到有新的消息到达
		 */
		List<String> messages = this.chatRepository.getMessages(messageIndex);
		if (!messages.isEmpty()) {
			deferredResult.setResult(messages);
		}

		return deferredResult;
	}

	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public void postMessage(@RequestParam String message) {

		this.chatRepository.addMessage(message);

		// 通知所有的人接受消息
		// 可以看一下redis分支更复杂，非阻塞的方法

		for (Entry<DeferredResult<List<String>>, Integer> entry : this.chatRequests.entrySet()) {
			List<String> messages = this.chatRepository.getMessages(entry.getValue());
			entry.getKey().setResult(messages);
		}
	}

}
