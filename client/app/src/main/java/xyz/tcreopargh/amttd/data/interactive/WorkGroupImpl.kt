package xyz.tcreopargh.amttd.data.interactive

import java.util.*

data class WorkGroupImpl(
    override var groupId: UUID = UUID.randomUUID(),
    override var name: String = "",
    override var timeCreated: Calendar = Calendar.getInstance(),
    override var usersInGroup: MutableList<UserImpl> = mutableListOf()
) : IWorkGroup {
    constructor(workGroup: IWorkGroup) : this(
        groupId = workGroup.groupId,
        name = workGroup.name,
        timeCreated = workGroup.timeCreated,
        usersInGroup = workGroup.usersInGroup.map { UserImpl(it) }.toMutableList()
    )
}
