from flask import Flask
from flask import request
from flask import jsonify
import base64
import torch
from torchvision import models
from PIL import Image
from torchvision import transforms

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
        model_conv.load_state_dict(torch.load('/Users/john_wei/PycharmProjects/CS125-Final-Project/last_save.pth'))
        x = Image.open('.../imageToSave.jpg')
        image_tensor = test_transforms(x).float()
        image_tensor = image_tensor.unsqueeze_(0)
        output = model_conv(image_tensor)
        index = output.data.cpu().numpy().argmax()
        return jsonify(list(lesion_type_dict.values())[index])

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