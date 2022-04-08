package com.ekdorn.silentium.utils

import com.ekdorn.silentium.core.Myte
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


class MyteSerializer : KSerializer<Myte> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Myte", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Myte) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder) = Myte(decoder.decodeString())
}
