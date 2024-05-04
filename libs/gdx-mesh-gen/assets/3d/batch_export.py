# exports each selected object into its own file

import bpy
import os
import random
# export to blend file location
basedir = os.path.dirname(bpy.data.filepath)

if not basedir:
    raise Exception("Blend file is not saved")

view_layer = bpy.context.view_layer

obj_active = view_layer.objects.active
selection = bpy.context.selected_objects


bpy.ops.object.select_all(action='DESELECT')


 
# Function to generate a random material 

def generate_random_material(): 
    # Choose a random material from the list 
    material = random.choice(materials) 
    # Create a new material based on the chosen material 
    new_material = bpy.data.materials.new(name="Random Material") 
    new_material.diffuse_color = material.diffuse_color 
    new_material.specular_color = material.specular_color 
    new_material.specular_intensity = material.specular_intensity 
    return new_material 
 
# Apply a random material to the selected object 

for obj in selection:
    
    selected_object = obj


    obj.select_set(True)

    # some exporters only use the active objectq
    view_layer.objects.active = obj

    name = bpy.path.clean_name(obj.name)
    fn = os.path.join(basedir, name)

    bpy.ops.export_scene.gltf(
            filepath=fn,
            use_selection=True,
            export_animations=False,
            export_current_frame=True,
            use_visible=False,
            export_materials='EXPORT',
            export_extras=True,
            export_colors=True,
            export_apply=True,
            export_texcoords=True,
            export_format='GLTF_EMBEDDED'
            )

    # Can be used for multiple formats
    # bpy.ops.export_scene.x3d(filepath=fn + ".x3d", use_selection=True)
    bpy.ops.export_scene.obj(filepath=fn+'.obj',use_selection=True)
    obj.select_set(False)

    print("written:", fn)


view_layer.objects.active = obj_active

for obj in selection:
    obj.select_set(True)
