package me.yoarchh.iridium.render.utils;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.util.Collection;

public class PointerUtils
{
    public static PointerBuffer stringColletionToPointerBuffer(Collection<String> collection)
    {
        MemoryStack stack = MemoryStack.stackGet();

        PointerBuffer buffer = stack.mallocPointer(collection.size());

        collection.stream()
                .map(stack::UTF8)
                .forEach(buffer::put);

        return buffer.rewind();
    }
}
