// ======================================================================
// \title  BasicTopologyAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Basic topology
// ======================================================================

#ifndef M_BasicTopologyAc_HPP
#define M_BasicTopologyAc_HPP

#include "Active.hpp"
#include "BasicTopologyDefs.hpp"
#include "Passive.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Constants
  // ----------------------------------------------------------------------

  namespace ConfigConstants {
    namespace active2 {
      enum {
        X = 0,
        Y = 1
      };
    }
  }

  namespace BaseIds {
    enum {
      active1 = 0x100,
      active2 = 0x200,
      active3 = 0x300,
      passive1 = 0x300,
      passive2 = 0x400,
    };
  }

  namespace CPUs {
    enum {
      active1 = 0,
    };
  }

  namespace InstanceIds {
    enum {
      active1,
      active2,
      active3,
      passive1,
      passive2,
    };
  }

  namespace Priorities {
    enum {
      active1 = 1,
    };
  }

  namespace QueueSizes {
    enum {
      active1 = 10,
      active2 = 10,
      active3 = 10,
    };
  }

  namespace StackSizes {
    enum {
      active1 = 1024,
    };
  }

  namespace TaskIds {
    enum {
      active1,
      active2,
      active3,
    };
  }

  // ----------------------------------------------------------------------
  // Component instances
  // ----------------------------------------------------------------------

  //! active1
  extern Active active1;

  //! active2
  extern Active active2;

  //! active3
  extern Active active3;

  //! passive1
  extern Passive passive1;

  //! passive2
  extern ConcretePassive passive2;

  // ----------------------------------------------------------------------
  // Helper functions
  // ----------------------------------------------------------------------

  //! Initialize components
  void initComponents(
      const TopologyState& state //!< The topology state
  );

  //! Configure components
  void configComponents(
      const TopologyState& state //!< The topology state
  );

  //! Set component base Ids
  void setBaseIds();

  //! Connect components
  void connectComponents();

  //! Start tasks
  void startTasks(
      const TopologyState& state //!< The topology state
  );

  //! Stop tasks
  void stopTasks(
      const TopologyState& state //!< The topology state
  );

  //! Free threads
  void freeThreads(
      const TopologyState& state //!< The topology state
  );

  //! Tear down components
  void tearDownComponents(
      const TopologyState& state //!< The topology state
  );

  // ----------------------------------------------------------------------
  // Setup and teardown functions
  // ----------------------------------------------------------------------

  //! Set up the topology
  void setup(
      const TopologyState& state //!< The topology state
  );

  //! Tear down the topology
  void teardown(
      const TopologyState& state //!< The topology state
  );

}

#endif
