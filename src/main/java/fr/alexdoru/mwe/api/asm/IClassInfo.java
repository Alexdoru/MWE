package fr.alexdoru.mwe.api.asm;

public interface IClassInfo {

    String internalName();

    default String desc() {
        return "L" + this.internalName() + ";";
    }

}
