package me.jakejmattson.discordkt.conversations

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.arguments.Argument

/** @suppress DSL backing
 *
 * @param discord The discord instance.
 * @param user The user that the conversation is happening with.
 * @param channel The channel that the conversation is happening in.
 * @param exitString A String entered by the user to exit the conversation.
 */
public class PlainConversationBuilder(
    discord: Discord,
    user: User,
    channel: MessageChannel,
    exitString: String? = null,
    timeout: Long,
) : ConversationBuilder(discord, user, channel, exitString, timeout) {
    /**
     * All ID's of messages sent by the bot in this conversation.
     */
    public val botMessageIds: MutableList<Snowflake> = mutableListOf()
    /**
     * The ID of the most recent message sent by the bot in this conversation.
     */
    public val previousBotMessageId: Snowflake
        get() = botMessageIds.last()

    @Throws(DmException::class)
    public override suspend fun <T> promptUntil(argument: Argument<*, T>, prompt: String, error: String, isValid: (T) -> Boolean): T {
        var value: T = prompt(argument, prompt)

        while (!isValid.invoke(value)) {
            channel.createMessage(error).also { it.let { botMessageIds.add(it.id) } }
            value = prompt(argument, prompt)
        }

        return value
    }

    @Throws(DmException::class, TimeoutException::class)
    public override suspend fun <I, O> prompt(argument: Argument<I, O>, text: String, embed: (suspend EmbedBuilder.() -> Unit)?): O {
        require(!argument.isOptional()) { "Conversation arguments cannot be optional" }

        val message = channel.createMessage {
            content = text.takeIf { it.isNotBlank() }

            if (embed != null) {
                val builder = EmbedBuilder()
                embed.invoke(builder)
                embeds.add(builder)
            }
        }

        botMessageIds.add(message.id)

        return retrieveValidTextResponse(argument)
    }

    @Throws(DmException::class, TimeoutException::class)
    public override suspend fun <T> promptButton(prompt: suspend ButtonPromptBuilder<T>.() -> Unit): T {
        val builder = ButtonPromptBuilder<T>()
        prompt.invoke(builder)
        val responder = builder.create(channel, channel.lastMessage!!.asMessage())

        botMessageIds.add(responder.ofMessage.id)

        return retrieveValidInteractionResponse(builder.valueMap)
    }

    @Throws(DmException::class, TimeoutException::class)
    public override suspend fun promptSelect(vararg options: String, embed: suspend EmbedBuilder.() -> Unit): String {
        val message = channel.createMessage {
            createSelectMessage(options, embed, this)
        }

        botMessageIds.add(message.id)

        return retrieveValidInteractionResponse(options.associateWith { it })
    }

    override suspend fun interactionIsOnLastBotMessage(interaction: ComponentInteraction): Boolean =
        interaction.message.id == previousBotMessageId
}