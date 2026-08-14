import os
from PIL import Image, ImageDraw

# Define the base paths
base_dir = os.getcwd()
gamefixes_item_dir = os.path.join(base_dir, "src", "main", "resources", "assets", "gamefixes", "textures", "item")
mc_block_dir = os.path.join(base_dir, "src", "main", "resources", "assets", "minecraft", "textures", "block")

# Ensure directories exist
os.makedirs(gamefixes_item_dir, exist_ok=True)
os.makedirs(mc_block_dir, exist_ok=True)

# Color Palette
terracotta = (150, 90, 60, 255)
dark_terracotta = (120, 70, 45, 255)
sulfur_yellow = (255, 220, 50, 255)
sulfur_highlight = (255, 245, 150, 255)

# --- 1. Refined Sulfur (Item) ---
# Creates a transparent background with a yellow crystal/pile shape
img_sulfur = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(img_sulfur)
draw.polygon([(8,2), (13,8), (8,14), (3,8)], fill=sulfur_yellow)
draw.polygon([(8,2), (10,8), (8,12), (6,8)], fill=sulfur_highlight)
img_sulfur.save(os.path.join(gamefixes_item_dir, "refined_sulfur.png"))

# --- 2. Blast Furnace Top ---
img_top = Image.new("RGBA", (16, 16), terracotta)
draw = ImageDraw.Draw(img_top)
draw.rectangle([2, 2, 13, 13], outline=dark_terracotta, width=1)
img_top.save(os.path.join(mc_block_dir, "blast_furnace_top.png"))

# --- 3. Blast Furnace Side ---
img_side = Image.new("RGBA", (16, 16), terracotta)
draw = ImageDraw.Draw(img_side)
draw.rectangle([0, 0, 15, 15], outline=dark_terracotta, width=2)
img_side.save(os.path.join(mc_block_dir, "blast_furnace_side.png"))

# --- 4. Blast Furnace Front (Off) ---
img_front = Image.new("RGBA", (16, 16), terracotta)
draw = ImageDraw.Draw(img_front)
draw.rectangle([0, 0, 15, 15], outline=dark_terracotta, width=2)
# Dark opening hole
draw.rectangle([4, 8, 11, 15], fill=(30, 30, 30, 255)) 
img_front.save(os.path.join(mc_block_dir, "blast_furnace_front.png"))

# --- 5. Blast Furnace Front (On) ---
img_front_on = Image.new("RGBA", (16, 16), terracotta)
draw = ImageDraw.Draw(img_front_on)
draw.rectangle([0, 0, 15, 15], outline=dark_terracotta, width=2)
# Bright fire opening
draw.rectangle([4, 8, 11, 15], fill=(255, 140, 0, 255)) 
draw.rectangle([6, 10, 9, 15], fill=(255, 200, 50, 255)) 
img_front_on.save(os.path.join(mc_block_dir, "blast_furnace_front_on.png"))

print("Placeholder textures generated and placed in the correct folders!")
