package webservices.telegram.service.chat;

public interface ChatEventListener {

	public void onEvent(ChatEvent event);

	public void onEvent(ParticipantEvent event);

}
