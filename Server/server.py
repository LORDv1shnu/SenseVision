from flask import Flask, request
from flask_cors import CORS
from PIL import Image
import torch
from transformers import AutoProcessor, AutoModelForCausalLM
import time

app = Flask(__name__)
CORS(app)  # Allows requests from your Android app

# Step 1: Choose device
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"🚀 [1/6] Using device: {device}")

# Step 2: Load GIT model on the selected device
print("🔄 [2/6] Loading GIT model...")
start_time = time.time()
processor = AutoProcessor.from_pretrained("microsoft/git-base")
model = AutoModelForCausalLM.from_pretrained("microsoft/git-base").to(device)
load_time = time.time() - start_time
print(f"✅ [3/6] Model loaded on {device} in {load_time:.2f} seconds.\n")

@app.route('/upload', methods=['POST'])
def upload_image():
    print("📥 [4/6] Receiving image from Android app...")

    try:
        # Save image data
        image_data = request.data
        with open("received.jpg", "wb") as f:
            f.write(image_data)
        print("✅ Image saved as 'received.jpg'")

        # Open and preprocess image
        print("🧠 Preprocessing image...")
        image = Image.open("received.jpg").convert("RGB")
        inputs = processor(images=image, return_tensors="pt").to(device)

        # Generate caption
        print("💬 Generating caption...")
        caption_start = time.time()
        generated_ids = model.generate(**inputs, max_length=50)
        caption = processor.batch_decode(generated_ids, skip_special_tokens=True)[0].strip()
        caption_end = time.time()

        print(f"✅ Caption generated in {caption_end - caption_start:.2f} sec on {device}: \"{caption}\"\n")
        return caption

    except Exception as e:
        print("❌ Error during processing:", e)
        return "Error generating caption", 500

if __name__ == '__main__':
    print("🌐 Starting server on http://0.0.0.0:5000")
    app.run(host='0.0.0.0', port=5000)
