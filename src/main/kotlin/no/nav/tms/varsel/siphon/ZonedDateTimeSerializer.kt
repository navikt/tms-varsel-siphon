package no.nav.tms.varsel.siphon

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException


class ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        val value = decoder.decodeString()

        return ZonedDateTime.parse(value)
    }

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)
}

class DefaultUtcZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext?): ZonedDateTime {
        return parser.text.parseAsZonedDateTime()
    }
}

fun String.parseAsZonedDateTime(defaultZoneId: ZoneId = ZoneId.of("Z")): ZonedDateTime {
    return try {
        return ZonedDateTime.parse(this)
    } catch (e: DateTimeParseException) {
        LocalDateTime.parse(this).atZone(defaultZoneId)
    }
}
