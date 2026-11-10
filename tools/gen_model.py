import math
from PIL import Image
GRAY=(172,174,180); WHITE=(238,239,243); DARK=(44,44,50); MID=(78,80,88); TIRE=(34,34,38); SPOKE=(150,152,160); HUB=(205,205,212)

# (nombre, grupo, (x,y,z,w,h,d), color)
big=[]  # cuboides de bb_main
def b(n,x,y,z,w,h,d,c): big.append((n,(x,y,z,w,h,d),c))
b('asiento_base',-6,-11,-5,12,1,11,GRAY)
b('asiento_cojin',-5.5,-12,-4.5,11,1,9,WHITE)
b('respaldo_marco',-6,-26,4.5,12,14,1.5,GRAY)
b('respaldo_cojin',-5,-24,3.5,10,11,1,WHITE)
for s,x in (('izq',-7.5),('der',6)):
    b('poste_'+s,x,-30,5,1.5,19,1.5,GRAY)
    b('mango_'+s,x,-31,6.5,1.5,1.5,5,DARK)
for s,x in (('izq',-8),('der',6)):
    b('apoyabrazos_'+s,x,-19,-4,2,1.5,10,GRAY)
    b('soporte_brazo_'+s,x+(0.25 if s=='izq' else 0.25),-17.5,-4,1.5,8,1.5,GRAY)
for s,x in (('izq',-7.5),('der',6)):
    b('riel_'+s,x,-10,-5.5,1.5,1.5,12,GRAY)
    b('placa_rueda_'+s,x,-12,0.5,1.5,8,3,GRAY)
b('travesano',-6,-8.5,-3,12,1,1.5,MID)
b('eje',-8,-7.5,1.4,16,1,1.2,DARK)
for s,x in (('izq',-7.25),('der',5.75)):
    b('pata_del_'+s,x,-9,-8.75,1.5,6.5,1.5,GRAY)
for s,x in (('izq',-7),('der',2)):
    b('reposapies_'+s,x,-3.5,-12,5,1,5,DARK)

wheel=[('llanta',(-1,-7,-2.9,2,14,5.8),TIRE),('rayo_a',(-1.1,-6,-0.4,2.2,12,0.8),SPOKE),
       ('rayo_b',(-1.1,-0.4,-6,2.2,0.8,12),SPOKE),('cubo',(-1.4,-1.5,-1.5,2.8,3,3),HUB)]
caster=[('ruedita',(-1,-2.5,-2.5,2,5,5),TIRE)]

# ---- empaquetado UV (shelf) ----
items=[]
def reg(key,dims,color): items.append([key,dims,color,None])
for n,d,c in big: reg('bb_'+n,d,c)
for n,d,c in wheel: reg('w_'+n,d,c)
for n,d,c in caster: reg('c_'+n,d,c)
def sz(d):
    w,h,dd=d[3],d[4],d[5]; return (2*math.ceil(dd)+2*math.ceil(w), math.ceil(dd)+math.ceil(h))
TW=TH=128
order=sorted(items,key=lambda i:-sz(i[1])[1])
x=y=0; rowh=0
for it in order:
    w,h=sz(it[1])
    if x+w>TW: x=0; y+=rowh; rowh=0
    it[3]=(x,y); x+=w; rowh=max(rowh,h)
assert y+rowh<=TH,(y,rowh)
img=Image.new('RGBA',(TW,TH),(0,0,0,0))
def shade(c,f): return tuple(max(0,min(255,int(v*f))) for v in c)+(255,)
def fill(rect,col):
    x0,y0,w,h=rect
    xa,xb=math.floor(x0),math.ceil(x0+w); ya,yb=math.floor(y0),math.ceil(y0+h)
    for i in range(xa,xb):
        for j in range(ya,yb):
            if 0<=i<TW and 0<=j<TH: img.putpixel((i,j),col)
for key,dims,color,(u,v) in items:
    w,h,d=dims[3],dims[4],dims[5]
    fill((u+d,v,w,d),shade(color,1.12))        # arriba
    fill((u+d+w,v,w,d),shade(color,0.68))      # abajo
    fill((u,v+d,d,h),shade(color,0.9))         # lado
    fill((u+d,v+d,w,h),shade(color,1.0))       # frente
    fill((u+d+w,v+d,d,h),shade(color,0.9))     # lado
    fill((u+2*d+w,v+d,w,h),shade(color,0.82))  # atras
img.save('src/main/resources/assets/silla/textures/entity/silla_de_ruedas.png')

uv={k:(u,v) for k,_,_,(u,v) in items}
def f(v): 
    s=('%.4f'%v).rstrip('0').rstrip('.'); return s+'F'
def cub(key,d): 
    u,v=uv[key]; return f'.uv({u}, {v}).cuboid({", ".join(f(t) for t in d)})'
java=[]
java.append('''package com.silla;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** Modelo de la silla de ruedas (generado: ver tools/gen_model.py). Textura 128x128. */
public class WheelchairModel extends EntityModel<WheelchairEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of("silla", "silla_de_ruedas"), "main");

    private final ModelPart root;
    private final ModelPart ruedaIzq, ruedaDer, ruedita1, ruedita2;
    /** Giro de las ruedas (radianes). Lo fija el renderer antes de dibujar. */
    public float roll;

    public WheelchairModel(ModelPart root) {
        this.root = root;
        this.ruedaIzq = root.getChild("rueda_izq");
        this.ruedaDer = root.getChild("rueda_der");
        this.ruedita1 = root.getChild("ruedita_izq");
        this.ruedita2 = root.getChild("ruedita_der");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        root.addChild("bb_main", ModelPartBuilder.create()''')
for n,d,c in big: java.append('                '+cub('bb_'+n,d))
java[-1]+=''',
                ModelTransform.pivot(0F, 24F, 0F));
'''
for side,px in (('izq',-9),('der',9)):
    java.append(f'''        ModelPartData rueda_{side} = root.addChild("rueda_{side}", ModelPartBuilder.create(), ModelTransform.pivot({f(px)}, 17F, 2F));''')
    for i,ang in enumerate((0,45,90,135)):
        java.append(f'''        rueda_{side}.addChild("llanta{i}", ModelPartBuilder.create(){cub('w_llanta',wheel[0][1])}, ModelTransform.rotation({f(math.radians(ang))}, 0F, 0F));''')
    for key,(n,d,c) in zip(('w_rayo_a','w_rayo_b','w_cubo'),wheel[1:]):
        java.append(f'''        rueda_{side}.addChild("{n}", ModelPartBuilder.create(){cub(key,d)}, ModelTransform.NONE);''')
for side,px in (('izq',-6.5),('der',6.5)):
    java.append(f'''        root.addChild("ruedita_{side}", ModelPartBuilder.create(){cub('c_ruedita',caster[0][1])}, ModelTransform.pivot({f(px)}, 21.5F, -8F));''')
java.append('''
        return TexturedModelData.of(data, 128, 128);
    }

    @Override
    public void setAngles(WheelchairEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ruedaIzq.pitch = roll;
        ruedaDer.pitch = roll;
        ruedita1.pitch = roll * 1.75F; // ruedas chicas giran mas rapido (radio menor)
        ruedita2.pitch = roll * 1.75F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }
}
''')
open('src/main/java/com/silla/WheelchairModel.java','w').write('\n'.join(java))
print("ok",len(items),"cuboides; textura 128x128")
