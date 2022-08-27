package imjimmyxd.simi.mywhatsapp.chat;

public class ChatObject {
    private String chatId, chatTitle;

    public ChatObject(String chatId/*, String chatTitle*/) {
        this.chatId = chatId;
//        this.chatTitle = chatTitle;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatTitle() {
        return chatTitle;
    }
}
