package me.aberrantfox.kjdautils.examples

import me.aberrantfox.kjdautils.api.dsl.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.arguments.UserArg
import net.dv8tion.jda.api.entities.User
import java.awt.Color

//Dependency injection works here too
@Convo
fun testConversation(config: MyCustomBotConfiguration) = conversation {
    name = "test-conversation"
    description = "Test conversation to test the implementation within KUtils."

    steps {
        step {
            prompt = embed {
                title = "Test Conversation"
                field {
                    name = "Step 1"
                    value = "To test various use cases I'd like you to tell me a user Id to start with."
                }
                color = Color.CYAN
            }
            expect = UserArg
        }
        step {
            prompt = embed {
                title = "Test Conversation"
                field {
                    name = "Step 2"
                    value = "Alright, now tell me a random sentence."
                }
                color = Color.CYAN
            }
            expect = SentenceArg
        }
    }

    onComplete {
        val user = it.responses.component1() as User
        val word = it.responses.component2() as String
        val summary = embed {
            title = "Summary - That's what you've told me"
            thumbnail = user.avatarUrl
            field {
                name = "Some user account"
                value = "The account of **${user.name}** was created on **${user.timeCreated}**."
            }
            field {
                name = "Random word"
                value = "You've said **$word**."
            }
            addBlankField(true)
            field {
                name = "Test Completed"
                value = "Thanks for participating!"
            }
            color = Color.GREEN
        }
        it.respond(summary)
    }
}
