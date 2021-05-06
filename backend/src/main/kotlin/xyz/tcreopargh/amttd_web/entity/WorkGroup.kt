package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "work_group")
data class WorkGroup(
    @Id
    @Column(
        name = "user_uuid",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(128)"
    )
    @Type(type = "uuid-char")
    val groupId: UUID = UUID.randomUUID(),
    val groupName: String = ""
) : EntityBase() {
}