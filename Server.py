from flask import Flask
from flask import request
from flask import jsonify
import base64
import torch
from torchvision import models
from PIL import Image
from torchvision import transforms
import torch.nn as nn
import json
app = Flask(__name__)

@app.route('/post', methods = ['POST'])
def eval():
    if request.method == 'POST':
        content = request.get_json()
        img = content['img']
        with open("imageToSave.jpg", "wb") as fh:
            fh.write(base64.decodebytes(img.encode()))
        model_conv = models.resnet50(pretrained=True)
        num_ftrs = model_conv.fc.in_features
        model_conv.fc = torch.nn.Linear(num_ftrs, 7)
        model_conv = nn.Sequential(model_conv, nn.Softmax())
        model_conv.load_state_dict(torch.load('/Users/john_wei/PycharmProjects/CS125-Final-Project/BEST_save.pth', map_location=('cpu')))
        x = Image.open('/Users/john_wei/PycharmProjects/CS125-Final-Project/imageToSave.jpg')
        image_tensor = test_transforms(x).float()
        image_tensor = image_tensor.unsqueeze_(0)
        output = model_conv(image_tensor).squeeze()
        print(output.tolist())
        list_of_porb = output.tolist()
        data = {
            'result': list_of_porb
        }
        jsonStr = json.dumps(data)
        return jsonStr

lesion_type_dict = {
        'nv': 'Melanocytic nevi',
        'mel': 'dermatofibroma',
        'bkl': 'Benign keratosis-like lesions ',
        'bcc': 'Basal cell carcinoma',
        'akiec': 'Actinic keratoses',
        'vasc': 'Vascular lesions',
        'df': 'Dermatofibroma'
    }


test_transforms = transforms.Compose([transforms.Resize(224),
                                      transforms.ToTensor(),
                                     ])


if __name__ == '__main__':
    app.run()