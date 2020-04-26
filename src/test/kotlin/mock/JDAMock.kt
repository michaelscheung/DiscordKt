package mock

import io.mockk.*
import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.discord.Discord
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.RestAction

val singleCategoryMock = mockk<Category> {
    every { id } returns FakeIds.Category
    every { name } returns FakeNames.Category_Single
}

val multiCategoryMock = mockk<Category> {
    every { id } returns FakeIds.Category
    every { name } returns FakeNames.Category_Multi
}

val singleRoleMock = mockk<Role> {
    every { id } returns FakeIds.Role
    every { name } returns FakeNames.Role_Single
}

val multiRoleMock = mockk<Role> {
    every { id } returns FakeIds.Role
    every { name } returns FakeNames.Role_Multi
}

val messageMock = mockk<Message> {
    every { id } returns FakeIds.Message
}

val guildMock = mockk<Guild> {
    every { id } returns FakeIds.Guild
    every { categories } returns listOf(singleCategoryMock, multiCategoryMock)
    every { roles } returns listOf(singleRoleMock, multiRoleMock)
}

val restActionMessageMock = mockk<RestAction<Message>> {
    every { complete() } returns messageMock
}

val channelMock = mockk<TextChannel> {
    every { retrieveMessageById(FakeIds.Message) } returns restActionMessageMock
}

val jdaMock = mockk<JDA> {
    every { getCategoryById(FakeIds.Category) } returns singleCategoryMock
    every { getCategoryById(FakeIds.Nothing) } returns null
    every { getRoleById(FakeIds.Role) } returns singleRoleMock
    every { getRoleById(FakeIds.Nothing) } returns null
    every { getTextChannelById(FakeIds.Channel) } returns channelMock
    every { getGuildById(FakeIds.Guild) } returns guildMock
}

val discordMock = mockk<Discord> {
    every { jda } returns jdaMock
}

val commandContainerMock = mockk<CommandsContainer> {

}

val commandEventMock = mockk<CommandEvent<*>> {
    every { container } returns commandContainerMock
    every { discord } returns discordMock
    every { guild } returns guildMock
    every { channel } returns channelMock
}
