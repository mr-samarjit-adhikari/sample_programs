#ifndef __SUNSETSOLAR_DISCOVERY_UI_H__
#define __SUNSETSOLAR_DISCOVERY_UI_H__

#include "ui_discovery.h"
#include "sunsetSolarDiscoEngine.h"

class sunsetSolarDiscoveryUi:public QWidget,
                             public Ui::SSSimpleDiscoForm
{
    Q_OBJECT
public:
    sunsetSolarDiscoveryUi(QWidget* parent=0);
    ~sunsetSolarDiscoveryUi();
    void showDiscoveredNodeInfo(unsigned int count);

public slots:
    /* Same push button will be clicked for starting/stopping 
     * the discovery actions  */
    void pbClicked();
    void showContextMenu(const QPoint&);
private:
    sunsetSolarDiscoEngine*     discoEngine;
};

#endif /*__SUNSET_SOLAR_DISCOVERY_UI_H__*/

