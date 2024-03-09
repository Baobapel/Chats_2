data class User(
    val id: Int,
    val username: String
)

data class Message(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    val isRead: Boolean
)

data class Chat(
    val id: Int,
    val user1Id: Int,
    val user2Id: Int,
    val messages: MutableList<Message>
)

interface ChatService {
    fun createChat(user1Id: Int, user2Id: Int): Chat
    fun deleteChat(chatId: Int)
    fun getChat(chatId: Int): Chat?
    fun getChats(): List<Chat>
    fun getUnreadChatsCount(): Int
    fun getLatestMessagesFromChats(): List<String>
    fun getMessagesFromChat(chatId: Int, userId: Int, count: Int): List<Message>
    fun createMessage(chatId: Int, senderId: Int, receiverId: Int, content: String): Message
    fun deleteMessage(chatId: Int, messageId: Int)
}

class ChatServiceImpl : ChatService {
    private var chats = mutableListOf<Chat>()
    private var messages = mutableListOf<Message>()

    override fun createChat(user1Id: Int, user2Id: Int): Chat {
        val chatId = chats.size + 1
        val chat = Chat(chatId, user1Id, user2Id, mutableListOf())
        chats.add(chat)
        return chat
    }

    override fun deleteChat(chatId: Int) {
        chats.removeIf { it.id == chatId }
    }

    override fun getChat(chatId: Int): Chat? {
        return chats.find { it.id == chatId }
    }

    override fun getChats(): List<Chat> {
        return chats
    }

    override fun getUnreadChatsCount(): Int {
        return chats.count { chat ->
            chat.messages.any { !it.isRead }
        }
    }

    override fun getLatestMessagesFromChats(): List<String> {
        return chats.map { chat ->
            chat.messages.lastOrNull()?.content ?: "no messages"
        }
    }


    override fun getMessagesFromChat(chatId: Int, userId: Int, count: Int): List<Message> {
        val chat = chats.find { it.id == chatId }
        return chat?.messages?.takeLast(count)?.also {
            it.forEach { message ->
                if (message.receiverId == userId) {
                    message.isRead
                }
            }
        } ?: emptyList()
    }

    override fun createMessage(chatId: Int, senderId: Int, receiverId: Int, content: String): Message {
        val messageId = messages.size + 1
        val message = Message(messageId, senderId, receiverId, content, false)
        messages.add(message)
        val chat = chats.find { it.id == chatId }
        chat?.messages?.add(message)
        return message
    }

    override fun deleteMessage(chatId: Int, messageId: Int) {
        val chat = chats.find { it.id == chatId }
        chat?.messages?.removeIf { it.id == messageId }
        messages.removeIf { it.id == messageId }
    }

}

fun main() {
    val chatService = ChatServiceImpl()

    val user1 = User(1, "Alice")
    val user2 = User(2, "Bob")

    val chat = chatService.createChat(user1.id, user2.id)
    println("Chat created: $chat")

    val message1 = chatService.createMessage(chat.id, user1.id, user2.id, "Hello, Bob!")
    println("Message created: $message1")

    val message2 = chatService.createMessage(chat.id, user2.id, user1.id, "Hi, Alice!")
    println("Message created: $message2")

    val messages = chatService.getMessagesFromChat(chat.id, user1.id, 5)
    println("Messages from chat with user1's id: $messages")

    val unreadChatsCount = chatService.getUnreadChatsCount()
    println("Unread chats count: $unreadChatsCount")

    val latestMessages = chatService.getLatestMessagesFromChats()
    println("Latest messages from chats: $latestMessages")

    chatService.deleteMessage(chat.id, message1.id)
    println("Message deleted: $message1")

    chatService.deleteChat(chat.id)
    println("Chat deleted: $chat")
}