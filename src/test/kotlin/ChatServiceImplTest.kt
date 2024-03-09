import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ChatServiceImplTest {
    private lateinit var chatService: ChatServiceImpl
    private lateinit var chats: MutableList<Chat>
    private lateinit var messages: MutableList<Message>

    @Before
    fun setUp() {
        chatService = ChatServiceImpl()
        chats = mutableListOf()
        messages = mutableListOf()
    }

    @Test
    fun testCreateChat() {
        val user1Id = 1
        val user2Id = 2
        val chat = Chat(1, user1Id, user2Id, mutableListOf())
        chats.add(chat)
        assertEquals(chat, chatService.createChat(user1Id, user2Id))
    }


    @Test
    fun testDeleteChat() {
        val chatId = 1
        val chat = Chat(chatId, 1, 2, mutableListOf())
        chats.add(chat)
        chatService.deleteChat(chatId)
        assertNull(chatService.getChat(chatId))
    }

    @Test
    fun testGetChat() {
        val chatId = 1
        val chat = Chat(chatId, 1, 2, mutableListOf())
        chatService.createChat(1,2)
        assertEquals(chat, chatService.getChat(chatId))
    }

    @Test
    fun testGetChats() {
        val chat1 = Chat(1, 1, 2, mutableListOf())
        val chat2 = Chat(2, 2, 3, mutableListOf())
        chatService.createChat(1, 2)
        chatService.createChat(2, 3)
        val expectedChats = listOf(chat1, chat2)
        assertEquals(expectedChats.size, chatService.getChats().size)
        assertEquals(expectedChats, chatService.getChats())
    }

    @Test
    fun testGetUnreadChatsCount() {
        chatService.createChat(1, 2)
        chatService.createChat(2, 3)

        // Добавляем непрочитанные сообщения в каждый чат при создании
        chatService.createMessage(1, 1, 2, "Hello")
        chatService.createMessage(2, 2, 3, "Hi")

        assertEquals(2, chatService.getUnreadChatsCount())
    }


    @Test
    fun testGetLatestMessagesFromChats() {
        // Создаем чаты
        val chat1 = Chat(1, 1, 2, mutableListOf())
        val chat2 = Chat(2, 2, 3, mutableListOf())
        val chat3 = Chat(3, 1, 3, mutableListOf())

        // Добавляем чаты в сервис
        chatService.createChat(1, 2)
        chatService.createChat(2, 3)
        chatService.createChat(1, 3)

        // Добавляем сообщения в чаты через сервис
        chatService.createMessage(1, 1, 2, "Message 1")
        chatService.createMessage(1, 1, 2, "Message 2")
        chatService.createMessage(1, 1, 2, "Message 3")
        chatService.createMessage(2, 2, 3, "Message 4")
        chatService.createMessage(2, 2, 3, "Message 5")
        chatService.createMessage(3, 1, 3, "Message 6")

        // Ожидаемые последние сообщения
        val expectedMessages = listOf("Message 3", "Message 5", "Message 6")

        // Получаем последние сообщения из чатов и сравниваем с ожидаемыми
        assertEquals(expectedMessages, chatService.getLatestMessagesFromChats())
    }



    @Test
    fun testGetMessagesFromChat() {
        // Создаем несколько чатов
        chatService.createChat(1, 2)
        chatService.createChat(2, 3)
        chatService.createChat(1, 3)

        // Добавляем сообщения в чаты
        chatService.createMessage(1, 1, 2, "Message 1")
        chatService.createMessage(1, 1, 2, "Message 2")
        chatService.createMessage(1, 1, 2, "Message 3")
        chatService.createMessage(2, 2, 3, "Message 4")
        chatService.createMessage(2, 2, 3, "Message 5")
        chatService.createMessage(3, 1, 3, "Message 6")

        // Получаем сообщения из первого чата для пользователя с id = 1
        val messagesFromChat1 = chatService.getMessagesFromChat(1, 1, 2)
        println("Messages from chat 1: $messagesFromChat1")

        // Проверяем, что размер списка сообщений равен 2
        assertEquals(2, messagesFromChat1.size)
        // Проверяем, что первое сообщение имеет содержимое "Message 3"
        assertEquals("Message 2", messagesFromChat1[0].content)
        // Проверяем, что второе сообщение имеет содержимое "Message 2"
        assertEquals("Message 3", messagesFromChat1[1].content)
    }



    @Test
    fun testCreateMessage() {
        val chatId = 1
        val senderId = 1
        val receiverId = 2
        val content = "Hello"
        val message = Message(1, senderId, receiverId, content, false)
        messages.add(message)
        assertEquals(message, chatService.createMessage(chatId, senderId, receiverId, content))
    }

    @Test
    fun testDeleteMessage() {
        val chatId = 1
        val messageId = 1
        chatService.createMessage(chatId, 1,1, "")
        chatService.deleteMessage(chatId, messageId)
        assertEquals(0, messages.size)
    }
}
