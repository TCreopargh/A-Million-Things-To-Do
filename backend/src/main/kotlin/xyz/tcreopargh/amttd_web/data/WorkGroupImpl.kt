package xyz.tcreopargh.amttd_web.data

import java.util.*

data class WorkGroupImpl(
    override var groupId: UUID = UUID.randomUUID(),
    override var name: String = "",
    override var timeCreated: Calendar = Calendar.getInstance(),
): IWorkGroup {
    constructor(workGroup: IWorkGroup) : this(workGroup.groupId, workGroup.name, workGroup.timeCreated)
}
