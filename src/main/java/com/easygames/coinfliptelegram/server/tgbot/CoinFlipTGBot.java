package com.easygames.coinfliptelegram.server.tgbot;

import com.easygames.coinfliptelegram.server.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class CoinFlipTGBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final GameService gameService;
    private final TelegramClient telegramClient;
    @Value("${BOT.TOKEN}")
    private String botToken;
    @Value("${BOT.NAME}")
    private String botUsername;
    @Value("${min_bet_amount}")
    private int MIN_BET_AMOUNT;

    public CoinFlipTGBot(
            GameService gameService,
            TelegramClient telegramClient) {
        this.gameService = gameService;
        this.telegramClient = telegramClient;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleTextMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
//                handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleTextMessage(Message message) throws TelegramApiException {
        String messageText = message.getText();
        long chatId = message.getChatId();
        String username = message.getFrom().getUserName();

        if (messageText.startsWith("/start")) {
            sendStartMessage(chatId);
        }
//        if (messageText.startsWith("/start") || messageText.equals("/play")) {
//            if (messageText.length() > 7) { // If there's a game code after /start
//                String gameCode = messageText.substring(7).trim();
//                if (gameService.isGameFinished(gameCode)) {
//                    String text = "This game has already finished. You can start a new game.";
//                    SendMessage backMessage = createMessageWithKeyboard(chatId, text,
//                            Collections.singletonList(createButton("Play Game", "play")));
//                    telegramClient.execute(backMessage);
//                } else {
//                    joinGame(chatId, gameCode, username);
//                }
//            } else {
//                sendStartMessage(chatId);
//            }
//        } else {
//            GameInfo game = gameService.getGameWaitingBet(chatId);
//            if (game.getState() == GameState.ENTERING_CUSTOM_BET) {
//                int betAmount = 0;
//                try {
//                    betAmount = Integer.parseInt(messageText);
//                } catch (NumberFormatException e) {
//                    sendMessage(chatId, "Your bet is invalid. Please enter only an integer and more than 1 flipky.");
//                }
////                handleCustomBet(chatId, username, betAmount, game);
//            }
//        }
    }

    //    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
//        String callbackData = callbackQuery.getData();
//        long chatId = callbackQuery.getMessage().getChatId();
//        String username = callbackQuery.getFrom().getUserName();
//
//        if (callbackData.startsWith("bet_")) {
//            handleBetCallback(chatId, username, callbackData);
//        } else if (callbackData.startsWith("copy_link_")) {
//            String gameCode = callbackData.substring("copy_link_".length());
//            handleCopyLink(chatId, gameCode);
//        } else if (callbackData.startsWith("choice_")) {
//            String[] parts = callbackData.split("_");
//            if (parts.length == 3) {
//                String choice = parts[1];
//                String gameCode = parts[2];
//                handleChoice(chatId, gameCode, choice);
//            }
//        } else if (callbackData.startsWith("cancel_")) {
//            String gameCode = callbackData.substring("cancel_".length());
//            handleCancelGame(chatId, gameCode);
//        } else if ("play".equals(callbackData)) {
//            handlePlayCommand(chatId, username);
//        } else {
//            sendMessage(chatId, "Unknown command. Please try again.");
//        }
//
//        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.getId());
//        telegramClient.execute(answer);
//    }
//
    private void sendStartMessage(long chatId) throws TelegramApiException {
        System.out.println(chatId);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Test text")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                .text("Welcome")
                                .webApp(WebAppInfo.builder()
                                        .url("https://localhost:3000/").build())
                                .build()
                        ))
                        .build())
                .build();
//        String messageText = String.format("Welcome to Coin Flip!\nYour current score: %d\nYour flipky: %d\n", user.getScore(), user.getFlipky());
//        SendMessage message = createMessageWithKeyboard(chatId, messageText,
//                Collections.singletonList(createButton("Play Game", "play")));
        telegramClient.execute(message);
    }

    //
//    private void handlePlayCommand(long chatId, String username) throws TelegramApiException {
//        User user = gameService.getOrCreateUser(chatId, username);
//        if (user.getFlipky() < MIN_BET_AMOUNT) {
//            sendMessage(chatId, "You don't have enough flipky to play. Please wait for daily bonus.");
//            return;
//        }
//        if (gameService.canGenerateNewGame(chatId)) {
//            sendBetMenu(chatId);
//        } else {
//            sendMessage(chatId, "You are already waiting for an opponent. Please wait or cancel the current game.");
//        }
//    }
//
//    private void sendBetMenu(long chatId) throws TelegramApiException {
//        SendMessage message = SendMessage.builder()
//                .chatId(String.valueOf(chatId))
//                .text("Please choose your bet amount:")
//                .replyMarkup(InlineKeyboardMarkup.builder()
//                        .keyboardRow(new InlineKeyboardRow(List.of(
//                                InlineKeyboardButton.builder().text("1 flipky").callbackData("bet_1").build(),
//                                InlineKeyboardButton.builder().text("5 flipky").callbackData("bet_5").build(),
//                                InlineKeyboardButton.builder().text("10 flipky").callbackData("bet_10").build()
//                        )))
//                        .keyboardRow(new InlineKeyboardRow(List.of(
//                                InlineKeyboardButton.builder().text("Custom amount").callbackData("bet_custom").build()
//                        )))
//                        .keyboardRow(new InlineKeyboardRow(List.of(
//                                InlineKeyboardButton.builder().text("Cancel").callbackData("bet_cancel").build()
//                        )))
//                        .build())
//                .build();
//        telegramClient.execute(message);
//    }
//
//    private void handleBetCallback(long chatId, String username, String callbackData) throws TelegramApiException {
//        String gameCode = gameService.generateGameCode(chatId, username);
//        switch (callbackData) {
//            case "bet_1":
//            case "bet_5":
//            case "bet_10":
//                int betAmount = Integer.parseInt(callbackData.split("_")[1]);
//                GameInfo game = gameService.getGame(gameCode);
//                gameService.setBetAmount(game, betAmount);
//                sendInviteMessage(chatId, gameCode, betAmount);
//                gameService.changeGameState(gameCode, GameState.WAITING_FOR_OPPONENT);
//                break;
//            case "bet_custom":
//                sendMessage(chatId, String.format("Please enter your bet amount (minimum %s flipky)\nThe bet can only be an integer:", MIN_BET_AMOUNT));
//                gameService.changeGameState(gameCode, GameState.ENTERING_CUSTOM_BET);
//                break;
//            case "bet_cancel":
//                String textMessage = "Betting process canceled. You can start a new game.";
//                SendMessage backMessage = createMessageWithKeyboard(chatId, textMessage,
//                        Collections.singletonList(createButton("Play Game", "play")));
//                telegramClient.execute(backMessage);
//
//                gameService.cancelGame(chatId, gameCode);
//                break;
//        }
//    }
//
//    private void handleCustomBet(long chatId, String username, int betAmount, GameInfo game) throws TelegramApiException {
//        try {
//            User user = gameService.getOrCreateUser(chatId, username);
//
//            if (betAmount < MIN_BET_AMOUNT) { // Use minBetAmount here
//                sendMessage(chatId, "The minimum bet is " + MIN_BET_AMOUNT + " flipky. Please enter a valid amount.");
//                return;
//            }
//
//            if (betAmount > user.getFlipky()) {
//                sendMessage(chatId, "You don't have enough flipky for this bet. Your current balance is " + user.getFlipky() + " flipky.");
//                return;
//            }
//
//            String gameCode = game.getGameCode();
//            gameService.setBetAmount(game, betAmount);
//            game.setBetAmount(betAmount);
//
//            sendInviteMessage(chatId, gameCode, betAmount);
//
//            gameService.changeGameState(game, GameState.WAITING_FOR_OPPONENT);
//        } catch (NumberFormatException e) {
//            sendMessage(chatId, "Please enter a valid number for the bet amount.");
//        }
//    }
//
//    private void sendInviteMessage(long chatId, String gameCode, int betAmount) throws TelegramApiException {
//        String inviteLink = "https://t.me/" + botUsername + "?start=" + gameCode;
//        String shareText = String.format("Let's play CoinFlip! Bet amount: %s flipky", betAmount);
//        String shareUrl = createShareUrl(inviteLink, shareText);
//
//        SendMessage message = SendMessage.builder()
//                .chatId(String.valueOf(chatId))
//                .text("Your game is ready! Bet amount: " + betAmount + " flipky. Choose how you want to share the invite:")
//                .replyMarkup(InlineKeyboardMarkup.builder()
//                        .keyboardRow(new InlineKeyboardRow(List.of(
//                                InlineKeyboardButton.builder().text("Share").url(shareUrl).build(),
//                                InlineKeyboardButton.builder().text("Copy Link").callbackData("copy_link_" + gameCode).build()
//                        )))
//                        .build())
//                .build();
//
//        Message sentMessage = telegramClient.execute(message);
//        gameService.storeOriginalMessage(gameCode, chatId, sentMessage.getMessageId());
//    }
//
//    private void handleCopyLink(long chatId, String gameCode) throws TelegramApiException {
//        String inviteLink = "https://t.me/" + botUsername + "?start=" + gameCode;
//        String escapedLink = inviteLink.replace("-", "\\-")
//                .replace(".", "\\.")
//                .replace("!", "\\!")
//                .replace("(", "\\(")
//                .replace(")", "\\)");
//
//        SendMessage message = SendMessage.builder()
//                .chatId(String.valueOf(chatId))
//                .text("Here's your invite link\\. Tap/click to copy:\n\n`" + escapedLink + "`")
//                .parseMode("MarkdownV2")
//                .build();
//
//        telegramClient.execute(message);
//    }
//
//    private String createShareUrl(String url, String text) {
//        String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
//        // Replace '+' with '%20' to ensure spaces are correctly encoded
//        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8).replace("+", "%20");
//        return "https://t.me/share/url?url=" + encodedUrl + "&text=" + encodedText;
//    }
//
//    private void updateSharedMessage(String gameCode) {
//        String updatedText = "This CoinFlip game has finished. You can start a new game by typing /play.";
//        // You'll need to store the original message ID and chat ID when sharing the link
//        // For this example, let's assume you have methods to retrieve them
//        Set<Long> originalChatIds = gameService.getOriginalChatId(gameCode);
//        int originalMessageId = gameService.getOriginalMessageId(gameCode);
//
//        originalChatIds.forEach(chatId -> {
//            EditMessageText editMessage = EditMessageText.builder()
//                    .chatId(String.valueOf(chatId))
//                    .messageId(originalMessageId)
//                    .text(updatedText)
//                    .build();
//            try {
//                telegramClient.execute(editMessage);
//            } catch (TelegramApiException e) {
//                log.error("Failed to update shared message for game {}: {}", gameCode, e.getMessage());
//            }
//        });
//
//
//    }
//
//    private void joinGame(long chatId, String gameCode, String username) throws TelegramApiException {
//        User user = gameService.getOrCreateUser(chatId, username);
//        GameInfo game = gameService.getGame(gameCode);
//        if (user.getFlipky() < game.getBetAmount()) {
//            sendMessage(chatId, "You don't have enough flipky to play. You need at least 10 flipky.");
//            return;
//        }
//        JoinGameResult result = gameService.joinGame(chatId, gameCode, username);
//        if (result.success) {
//            sendMessage(chatId, "Game started. " + result.message + " is making a choice.");
//            sendMessageWithChoiceButtons(result.initiatorId, username + " accepted invitation. Game started! Choose Heads or Tails:", gameCode);
//        } else {
//            sendMessage(chatId, result.message);
//        }
//    }
//
//    private void handleChoice(long chatId, String gameCode, String choice) throws TelegramApiException {
//        if (!gameService.canMakeChoice(chatId, gameCode)) {
//            sendMessage(chatId, "It's not your turn or you're not in this game.");
//            return;
//        }
//        GameInfo game = gameService.getGame(gameCode);
//        GameResult result = gameService.makeChoice(chatId, gameCode, choice);
//        if (result == null) {
//            sendMessage(chatId, "Unable to make a choice. The game might have ended or you're not a participant.");
//            return;
//        }
//        User player = gameService.getOrCreateUser(chatId, null);
//        User opponent = gameService.getOrCreateUser(result.opponentId, null);
//        int playerScore = player.getScore();
//        int playerFlipky = player.getFlipky();
//        int opponentScore = opponent.getScore();
//        int opponentFlipky = opponent.getFlipky();
//
//        int betAmount = game.getBetAmount(); // You can make this dynamic based on user input
//        gameService.saveFinishedGame(gameCode, chatId, result.opponentId, betAmount, choice, result.coinResult, result.playerWins ? "initiator" : "opponent");
//
//        String playerMessage = String.format("The coin shows %s. You chose %s. You %s!\nYour current score %s. Your current flipky amount %s",
//                result.coinResult, result.playerChoice, result.playerWins ? "win" : "lose", playerScore, playerFlipky);
//        String opponentMessage = String.format("The coin shows %s. Your opponent chose %s. You %s!\nYour current score %s. Your current flipky amount %s",
//                result.coinResult, result.playerChoice, result.playerWins ? "lose" : "win", opponentScore, opponentFlipky);
//
//        sendMessageWithPlayAgainButton(chatId, playerMessage);
//        sendMessageWithPlayAgainButton(result.opponentId, opponentMessage);
//
//        // Update the shared message to indicate the game has finished
//        updateSharedMessage(gameCode);
//        // Finish the game and clean up
//        gameService.finishGame(gameCode);
//    }
//
//    private void handleCheckStatus(long chatId) throws TelegramApiException {
//        User user = gameService.getOrCreateUser(chatId, null);
//        String statusMessage = String.format("Your current score: %d\nYour flipky: %d", user.getScore(), user.getFlipky());
//        sendMessage(chatId, statusMessage);
//    }
//
//    private void handleCancelGame(long chatId, String gameCode) throws TelegramApiException {
//        if (gameService.cancelGame(chatId, gameCode)) {
//            sendMessage(chatId, "Game cancelled. You can start a new game.");
//            updateSharedMessage(gameCode);
//        } else {
//            sendMessage(chatId, "Unable to cancel the game. It may have already started or doesn't exist.");
//        }
//    }
//
//    private SendMessage createMessageWithKeyboard(long chatId, String text, List<InlineKeyboardButton> buttons) {
//        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
//                .chatId(String.valueOf(chatId))
//                .text(text);
//
//        if (!buttons.isEmpty()) {
//            InlineKeyboardMarkup markupInline = InlineKeyboardMarkup.builder()
//                    .keyboardRow(new InlineKeyboardRow(buttons))
//                    .build();
//            messageBuilder.replyMarkup(markupInline);
//        }
//
//        return messageBuilder.build();
//    }
//
//    private InlineKeyboardButton createButton(String text, String callbackData) {
//        return InlineKeyboardButton.builder()
//                .text(text)
//                .callbackData(callbackData)
//                .build();
//    }
//
//    private void sendMessage(long chatId, String text) throws TelegramApiException {
//        SendMessage message = SendMessage.builder()
//                .chatId(String.valueOf(chatId))
//                .text(text)
//                .build();
//        telegramClient.execute(message);
//    }
//
//
//    private void sendMessageWithChoiceButtons(long chatId, String text, String gameCode) throws TelegramApiException {
//        SendMessage message = createMessageWithKeyboard(chatId, text,
//                List.of(
//                        createButton("Heads", "choice_heads_" + gameCode),
//                        createButton("Tails", "choice_tails_" + gameCode)
//                ));
//        telegramClient.execute(message);
//    }
//
//    private void sendMessageWithPlayAgainButton(long chatId, String text) throws TelegramApiException {
//        SendMessage message = createMessageWithKeyboard(chatId, text,
//                Collections.singletonList(createButton("Play Again", "play")));
//        telegramClient.execute(message);
//    }
//
//
    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Bot registered. Running state: {}", botSession.isRunning());
    }
}