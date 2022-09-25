package me.jakejmattson.discordkt.arguments

import me.jakejmattson.discordkt.commands.DiscordContext
import me.jakejmattson.discordkt.dsl.internalLocale

/**
 * Accepts any (single) argument. Does not accept empty strings.
 */
public open class AnyArg(override val name: String = "Any",
                         override val description: String = internalLocale.anyArgDescription) : StringArgument<String> {
    /**
     * Accepts any (single) argument. Does not accept empty strings.
     */
    public companion object : AnyArg()

    override suspend fun transform(input: String, context: DiscordContext): Result<String> = Success(input)
}