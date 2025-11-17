package com.bluebed;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;

public class CrossyTitle {

    private final Entity background;
    private final Entity title;

    public CrossyTitle(Pos pos, Instance instance) {
        Vec dir = pos.direction().normalize();

        Pos finalPos = pos.add(0, 1, 0);

        background = new Entity(EntityType.TEXT_DISPLAY);
        background.setNoGravity(true);

        background.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(Component.text("\uE05A"));
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
            meta.setBackgroundColor(0);
        });
        background.setInstance(instance, finalPos.mul(1.5));

        title = new Entity(EntityType.TEXT_DISPLAY);
        title.setNoGravity(true);
        title.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(Component.text("\uE05B"));
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
            meta.setBackgroundColor(0);
        });
        title.setInstance(instance, finalPos.mul(1.6));
    }


}
