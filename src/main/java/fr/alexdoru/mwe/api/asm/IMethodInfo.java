package fr.alexdoru.mwe.api.asm;

public interface IMethodInfo {

    int opcode();

    String owner();

    String methodName();

    String desc();

}
