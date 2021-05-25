package xyz.tcreopargh.amttd_web.common.data

import java.util.*

data class WorkGroupImpl(
    override var groupId: UUID = UUID.randomUUID(),
    override var name: String = "",
    override var timeCreated: Calendar = Calendar.getInstance(),
    override var usersInGroup: MutableList<UserImpl> = mutableListOf(),
    override var leader: UserImpl? = null
) : IWorkGroup {
    constructor(workGroup: IWorkGroup) : this(
        groupId = workGroup.groupId,
        name = workGroup.name,
        timeCreated = workGroup.timeCreated,
        usersInGroup = workGroup.usersInGroup.map { UserImpl(it) }.toMutableList(),
        leader = workGroup.leader?.let { UserImpl(it) }
    )
}
