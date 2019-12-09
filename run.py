import os
import torchvision.models as models
from torchvision import transforms
from glob import glob
import pandas as pd
import torch
import torch.nn as nn
from sklearn.model_selection import train_test_split
import torch
from PIL import Image
from torch.utils import data
from torch.utils.data import Dataset, DataLoader
import torchvision.transforms as trf
import matplotlib.pyplot as plt
import numpy as np
from utils import *


class Dataset(Dataset):
    'Characterizes a dataset for PyTorch'
    def __init__(self, df, transform=None):
        'Initialization'
        self.df = df
        self.transform = transform

    def __len__(self):
        'Denotes the total number of samples'
        return len(self.df)

    def __getitem__(self, index):
        'Generates one sample of data'
        # Load data and get label
        X = Image.open(self.df['path'][index])
        y = torch.tensor(int(self.df['cell_type_idx'][index]))

        if self.transform:
            X = self.transform(X)

        return X, y
# Define the parameters for the dataloader
params = {'batch_size': 4,
          'shuffle': True,
          'num_workers': 6}

composed = trf.Compose([trf.RandomHorizontalFlip(), trf.RandomVerticalFlip(),
                        trf.CenterCrop(256), trf.RandomCrop(224), trf.ToTensor(),
                        trf.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])])
###########################################################手动分割dataloader

test_transforms = transforms.Compose([transforms.Resize(224),
                                      transforms.ToTensor(),
                                     ])

def inference():
    x = Image.open('D:\\FinalProject\\input\\HAM10000_images_part_1\\ISIC_0024306.jpg')
    model_conv.load_state_dict(torch.load('D:\\FinalProject\\last_save.pth'))
    model_conv.eval()

def predict_image(image):
    image_tensor = test_transforms(image).float()
    image_tensor = image_tensor.unsqueeze_(0)
    output = model_conv(image_tensor)
    index = output.data.gpu().numpy()
    return index

if __name__ == '__main__':

    base_skin_dir = os.path.join('D:\\FinalProject', 'input')

    imageid_path_dict = {os.path.splitext(os.path.basename(x))[0]: x
                     for x in glob(os.path.join(base_skin_dir, '*', '*.jpg'))}

    lesion_type_dict = {
        'nv': 'Melanocytic nevi',
        'mel': 'dermatofibroma',
        'bkl': 'Benign keratosis-like lesions ',
        'bcc': 'Basal cell carcinoma',
        'akiec': 'Actinic keratoses',
        'vasc': 'Vascular lesions',
        'df': 'Dermatofibroma'
    }
    device = 'cuda'
    tile_df = pd.read_csv(os.path.join(base_skin_dir, 'HAM10000_metadata.csv'))
    tile_df['path'] = tile_df['image_id'].map(imageid_path_dict.get)
    tile_df['cell_type'] = tile_df['dx'].map(lesion_type_dict.get)
    tile_df['cell_type_idx'] = pd.Categorical(tile_df['cell_type']).codes
    tile_df[['cell_type_idx', 'cell_type']].sort_values('cell_type_idx').drop_duplicates()

    model_conv = models.resnet50(pretrained=True)
    num_ftrs = model_conv.fc.in_features
    model_conv.fc = torch.nn.Linear(num_ftrs, 7)
    model_conv = nn.Sequential(model_conv, nn.Softmax())
    model_conv = model_conv.to(device)
    train_df, test_df = train_test_split(tile_df, test_size=0.1)
    # We can split the test set again in a validation set and a true test set:
    validation_df, test_df = train_test_split(test_df, test_size=0.5)
    train_df = train_df.reset_index()
    validation_df = validation_df.reset_index()
    test_df = test_df.reset_index()

    training_set = Dataset(train_df, transform=composed)
    training_generator = data.DataLoader(training_set, **params)

    validation_set = Dataset(validation_df, transform=composed)
    validation_generator = data.DataLoader(validation_set, **params)

    optimizer = torch.optim.Adam(model_conv.parameters(), lr=1e-7)
    criterion = torch.nn.CrossEntropyLoss()


    device = 'cuda'
    max_epochs = 50
    trainings_error = []
    validation_error = []
    best_loss = 1000


    for epoch in range(max_epochs):
        print('epoch:', epoch)
        count_train = 0
        trainings_error_tmp = []
        model_conv.train()
        for data_sample, y in training_generator:
            data_gpu = data_sample.to(device)
            y_gpu = y.to(device)
            #output = model_conv(data_sample)
            output = model_conv(data_gpu)
            #err = criterion(output, y)
            err = criterion(output, y_gpu)
            err.backward()
            optimizer.step()
            trainings_error_tmp.append(err.item())
            count_train += 1
            if count_train >= 100:
                count_train = 0
                mean_trainings_error = np.mean(trainings_error_tmp)
                trainings_error.append(mean_trainings_error)
                print('trainings error:', mean_trainings_error)
                torch.save(model_conv.state_dict(), 'D:\\FinalProject\\last_save{}.pth'.format(epoch))
                break
        with torch.set_grad_enabled(False):
            validation_error_tmp = []
            count_val = 0
            model_conv.eval()
            for data_sample, y in validation_generator:
                data_gpu = data_sample.to(device)
                y_gpu = y.to(device)
                #output = model_conv(data_sample)
                output = model_conv(data_gpu)
                #err = criterion(output, y)
                err = criterion(output, y_gpu)
                validation_error_tmp.append(err.item())
                count_val += 1
                if count_val >= 10:
                    count_val = 0
                    mean_val_error = np.mean(validation_error_tmp)
                    validation_error.append(mean_val_error)
                    print('validation error:', mean_val_error)
                    if mean_val_error < best_loss:
                        best_loss = mean_val_error
                        torch.save(model_conv.state_dict(), 'D:\\FinalProject\\BEST_save.pth')
                        print("Better model found!")
                    break
    plt.plot(trainings_error, label = 'training error')
    plt.plot(validation_error, label = 'validation error')
    plt.legend()
    plt.show()

    for i in range(20):
        model_conv = models.resnet50(pretrained=True)
        num_ftrs = model_conv.fc.in_features
        model_conv.fc = torch.nn.Linear(num_ftrs, 7)
        model_conv = nn.Sequential(model_conv, nn.Softmax())
        model_conv.load_state_dict(torch.load('D:\\FinalProject\\BEST_save.pth'))
        model_conv1 = model_conv
        model_conv = model_conv.to(device)
        x = Image.open('D:\\FinalProject\\input\\HAM10000_images_part_1\\ISIC_0024306.jpg')
        image_tensor = test_transforms(x).float()
        image_tensor = image_tensor.unsqueeze_(0).to(device)
        output = model_conv1(image_tensor)
        index = output.data.cpu().numpy()
        print("Model {}".format(i))
        print(index)

        model_conv.eval()
        test_set = Dataset(validation_df, transform=composed)
        test_generator = data.SequentialSampler(validation_set)
        result_array = []
        gt_array = []
        for i in test_generator:
            data_sample, y = validation_set.__getitem__(i)
            #data_sample = data_sample.unsqueeze(0)
            data_gpu = data_sample.unsqueeze(0).to(device)
            #output = model_conv(data_sample)
            output = model_conv(data_gpu)
            result = torch.argmax(output)
            result_array.append(result.item())
            gt_array.append(y.item())
        correct_results = np.array(result_array)==np.array(gt_array)
        sum_correct = np.sum(correct_results)
        accuracy = sum_correct/test_generator.__len__()
        print(accuracy)
