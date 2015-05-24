///////////////////////////////////////////////////////////////////////
// $Author: rhs041163 $
// // $Id: discoveryUi.cpp,v 1.6 2012/11/28 14:19:21 rhs041163 Exp $
// // $Date: 2012/11/28 14:19:21 $
// // $Revision: 1.6 $
// ///////////////////////////////////////////////////////////////////////

#include <iostream>
#include <QPoint>
#include <QMenu>
#include <QTreeWidgetItem>

#include "discoveryUi.h"

#define PBTEXT_DISCOVER  "Discover"
#define PBTEXT_STOP      "Stop"

sunsetSolarDiscoveryUi::sunsetSolarDiscoveryUi(QWidget* parent){
    setupUi(this);

    QStringList cbList;
    cbList.append("LAN Discovery");
    cbList.append("SUBNET Discovery");

    /* Populate the discovery type combobox.*/
    SSDiscoTypeCB->addItems(cbList);
    SSTreeWidget->setContextMenuPolicy(Qt::CustomContextMenu);

    /* Connect different signals */
    connect((QObject*)SSDiscoverPB,SIGNAL(clicked()),
             SLOT(pbClicked()));
    connect(SSTreeWidget,SIGNAL(customContextMenuRequested(const QPoint&)),
             SLOT(showContextMenu(const QPoint&)));
}

sunsetSolarDiscoveryUi::~sunsetSolarDiscoveryUi(){

}

void sunsetSolarDiscoveryUi::pbClicked(){
    //std::cout<<" pbClicked clicked!!"<<std::endl;
    QString  pbText = this->SSDiscoverPB->text();

    this->SSDiscoverPB->setDisabled(true);
    if(pbText == QString(PBTEXT_DISCOVER)){
        this->discoEngine = new sunsetSolarDiscoEngine();

        if(this->discoEngine->configure(NULL)<0){ // for now send NULL
            delete this->discoEngine;
            this->discoEngine = NULL;
            this->SSDiscoverPB->setEnabled(true);
            return;
        }
        this->discoEngine->startEngine();
        // Enable stop and start disco Engine
        this->SSDiscoverPB->setText(QString(PBTEXT_STOP));

    }
    else if(pbText == QString(PBTEXT_STOP)){
        // Update the node informations
        this->showDiscoveredNodeInfo(DATAQ_SIZE);

        this->discoEngine->stopEngine();
        delete this->discoEngine;
        this->discoEngine = NULL;
        this->SSDiscoverPB->setText(QString(PBTEXT_DISCOVER));
    }
        
    this->SSDiscoverPB->setEnabled(true);
}

void
sunsetSolarDiscoveryUi::showContextMenu(const QPoint& pos)
{
#define ACTION_REFRESH   "Refresh"
#define ACTION_CLEAR     "Clear"
    QPoint globalPos = this->SSTreeWidget->mapToGlobal(pos);

    QMenu  showMenu;
    showMenu.addAction(ACTION_REFRESH);
    showMenu.addAction(ACTION_CLEAR);

    QAction*  action = showMenu.exec(globalPos);

    if(action){
        if(action->text() == QString(ACTION_REFRESH)){
            this->showDiscoveredNodeInfo(10);
        }
        else if(action->text() == QString(ACTION_CLEAR)){
            for(;this->SSTreeWidget->topLevelItemCount();){
                this->SSTreeWidget->takeTopLevelItem(0);
            }
        }
    }
}

void 
sunsetSolarDiscoveryUi::showDiscoveredNodeInfo(unsigned int count)
{
    unsigned int    lcount = count;

    if(this->discoEngine){
        sunsetSolarDiscoDataObj* data = this->discoEngine->readDiscoData();
        while(NULL != data && (lcount--)>0){
            QString name  = QString(data->getName().c_str());
            QString value = QString(data->getValue().c_str());
            delete data;

            /* Widget specific addition */
            QTreeWidgetItem*  treeWidgetItem = new QTreeWidgetItem();
            treeWidgetItem->setText(0,QString(name));
            treeWidgetItem->setText(1,QString(value));
            this->SSTreeWidget->addTopLevelItem(treeWidgetItem);

            data = this->discoEngine->readDiscoData();
        }
    }
}
