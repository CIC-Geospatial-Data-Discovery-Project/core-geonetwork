/*
 * Copyright (C) 2001-2016 Food and Agriculture Organization of the
 * United Nations (FAO-UN), United Nations World Food Programme (WFP)
 * and United Nations Environment Programme (UNEP)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
 * Rome - Italy. email: geonetwork@osgeo.org
 */

(function() {
  goog.provide('gn_admin_controller');























  goog.require('gn_admin_menu');
  goog.require('gn_adminmetadata_controller');
  goog.require('gn_admintools_controller');
  goog.require('gn_cat_controller');
  goog.require('gn_classification_controller');
  goog.require('gn_dashboard_controller');
  goog.require('gn_harvest_controller');
  goog.require('gn_report_controller');
  goog.require('gn_settings_controller');
  goog.require('gn_standards_controller');
  goog.require('gn_usergroup_controller');

  var module = angular.module('gn_admin_controller',
      ['gn_dashboard_controller', 'gn_usergroup_controller',
       'gn_admintools_controller', 'gn_settings_controller',
       'gn_adminmetadata_controller', 'gn_classification_controller',
       'gn_harvest_controller', 'gn_standards_controller',
       'gn_report_controller', 'gn_admin_menu']);


  var tplFolder = '../../catalog/templates/admin/';

  module.config(['$routeProvider', function($routeProvider) {
    $routeProvider.
        when('/metadata', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminMetadataController'}).
        when('/metadata/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminMetadataController'}).
        when('/metadata/schematron/:schemaName', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminMetadataController'}).
        when('/metadata/schematron/:schemaName/:schematronId', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminMetadataController'}).
        when('/metadata/:tab/:metadataAction/:schema', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminMetadataController'}).
        when('/dashboard', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnDashboardController'}).
        when('/dashboard/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnDashboardController'}).
        when('/organization', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnUserGroupController'}).
        when('/organization/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnUserGroupController'}).
        when('/organization/:tab/:userOrGroup', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnUserGroupController'}).
        when('/classification', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnClassificationController'}).
        when('/classification/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnClassificationController'}).
        when('/tools', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminToolsController'}).
        when('/tools/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminToolsController'}).
        when('/tools/:tab/select/:selectAll/process/:processId', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnAdminToolsController'}).
        when('/harvest', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnHarvestController'}).
        when('/harvest/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnHarvestController'}).
        when('/settings', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnSettingsController'}).
        when('/settings/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnSettingsController'}).
        when('/standards', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnStandardsController'}).
        when('/reports', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnReportController'}).
        when('/reports/:tab', {
          templateUrl: tplFolder + 'page-layout.html',
          controller: 'GnReportController'}).
        otherwise({templateUrl: tplFolder + 'admin.html'});
  }]);

  /**
   * The admin console controller.
   *
   * Example:
   *
   *     <body ng-controller="GnAdminController">
   */
  module.controller('GnAdminController', [
    '$scope', '$http', '$q', '$rootScope', '$route', '$routeParams',
    'gnUtilityService', 'gnAdminMenu',
    function($scope, $http, $q, $rootScope, $route, $routeParams,
        gnUtilityService, gnAdminMenu) {
      $scope.menu = gnAdminMenu;
      /**
       * Define menu position on the left (nav-stacked)
       * or on top of the page.
       */
      $scope.navStacked = true;

      $scope.getTpl = function(pageMenu) {
        $scope.type = pageMenu.defaultTab;
        $.each(pageMenu.tabs, function(index, value) {
          if (value.type === $routeParams.tab) {
            $scope.type = $routeParams.tab;
          }
        });
        return tplFolder + pageMenu.folder + $scope.type + '.html';
      };

      $scope.csvExport = function(json, e) {
        $(e.target).next('pre.gn-csv-export').remove();
        $(e.target).after("<pre class='gn-csv-export'>" +
                gnUtilityService.toCsv(json) + '</pre>');
      };

      /**
       * Return menu Angular route or GeoNetwork legacy URLs
       * according to $scope.menu configuration.
       */
      $scope.getMenuUrl = function(menu) {
        if (menu.route) {
          return menu.route;
        } else if (menu.url) {
          return $scope.url + menu.url;
        }
      };

      /**
       * Return menu according to user profile
       */
      $scope.getMenu = function() {
        return $scope.menu[$scope.user.profile];
      };
    }]);

})();
